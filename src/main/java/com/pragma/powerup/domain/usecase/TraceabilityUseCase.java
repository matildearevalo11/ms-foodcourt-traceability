package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.model.OrderEfficiency;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.ILoggedUserPort;
import com.pragma.powerup.domain.spi.IOwnerValidationPort;
import com.pragma.powerup.domain.enums.OrderStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceabilityUseCase implements ITraceabilityServicePort {
    private final ITraceabilityPersistencePort persistencePort;
    private final ILoggedUserPort loggedUserPort;
    private final IOwnerValidationPort ownerValidationPort;

    @Override
    public Traceability createTraceability(Traceability traceability) {
        traceability.setId(UUID.randomUUID().toString());
        traceability.setChangedAt(Instant.now());
        return persistencePort.save(traceability);
    }

    @Override
    public List<Traceability> getOrderTraceability(Long orderId) {
        return persistencePort.findByOrderIdAndCustomerId(orderId, loggedUserPort.getUserId());
    }

    @Override
    public List<OrderEfficiency> getOrderEfficiencies(Long restaurantId) {
        ownerValidationPort.validateOwnership(restaurantId);
        return completedOrders(restaurantId);
    }

    @Override
    public List<EmployeeEfficiency> getEmployeeEfficiencyRanking(Long restaurantId) {
        ownerValidationPort.validateOwnership(restaurantId);
        Map<Long, List<OrderEfficiency>> ordersByEmployee = new LinkedHashMap<>();
        completedOrders(restaurantId).forEach(order -> ordersByEmployee
                .computeIfAbsent(order.employeeId(), ignored -> new ArrayList<>()).add(order));

        List<EmployeeEfficiency> sorted = ordersByEmployee.entrySet().stream()
                .map(entry -> new EmployeeEfficiency(null, entry.getKey(), (long) entry.getValue().size(),
                        entry.getValue().stream().mapToLong(OrderEfficiency::durationSeconds)
                                .average().orElse(0)))
                .sorted(Comparator.comparing(EmployeeEfficiency::averageDurationSeconds)
                        .thenComparing(EmployeeEfficiency::employeeId))
                .toList();

        List<EmployeeEfficiency> ranked = new ArrayList<>(sorted.size());
        for (int index = 0; index < sorted.size(); index++) {
            EmployeeEfficiency efficiency = sorted.get(index);
            ranked.add(new EmployeeEfficiency(index + 1, efficiency.employeeId(), efficiency.completedOrders(),
                    efficiency.averageDurationSeconds()));
        }
        return List.copyOf(ranked);
    }

    private List<OrderEfficiency> completedOrders(Long restaurantId) {
        Map<Long, List<Traceability>> eventsByOrder = new LinkedHashMap<>();
        persistencePort.findByRestaurantId(restaurantId).forEach(event ->
                eventsByOrder.computeIfAbsent(event.getOrderId(), ignored -> new ArrayList<>()).add(event));
        return eventsByOrder.values().stream()
                .map(this::toOrderEfficiency)
                .filter(Objects::nonNull)
                .toList();
    }

    private OrderEfficiency toOrderEfficiency(List<Traceability> events) {
        Traceability started = events.stream()
                .filter(event -> event.getNewStatus() == OrderStatus.PENDING)
                .findFirst()
                .orElse(null);
        Traceability completed = events.stream()
                .filter(event -> event.getNewStatus() == OrderStatus.DELIVERED)
                .reduce((first, second) -> second)
                .orElse(null);
        if (started == null || completed == null || completed.getEmployeeId() == null
                || completed.getChangedAt().isBefore(started.getChangedAt())) {
            return null;
        }
        return new OrderEfficiency(started.getOrderId(), completed.getEmployeeId(), started.getChangedAt(),
                completed.getChangedAt(), Duration.between(started.getChangedAt(), completed.getChangedAt()).getSeconds());
    }
}
