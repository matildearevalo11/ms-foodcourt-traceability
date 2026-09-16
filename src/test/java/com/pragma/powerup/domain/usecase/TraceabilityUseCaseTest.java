package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pragma.powerup.domain.enums.OrderStatus;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.ILoggedUserPort;
import com.pragma.powerup.domain.spi.IOwnerValidationPort;
import com.pragma.powerup.domain.exception.AuthorizationException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceabilityUseCaseTest {
    @Mock
    ITraceabilityPersistencePort persistencePort;
    @Mock
    ILoggedUserPort loggedUserPort;
    @Mock
    IOwnerValidationPort ownerValidationPort;

    @Test
    void assignsIdentityAndTimestampBeforeSaving() {
        Traceability traceability = new Traceability(
                null, 30L, 20L, 5L, null, null, OrderStatus.PENDING, null);
        when(persistencePort.save(traceability)).thenReturn(traceability);

        Traceability result = new TraceabilityUseCase(persistencePort, loggedUserPort, ownerValidationPort)
                .createTraceability(traceability);

        assertThat(result.getId()).isNotBlank();
        assertThat(result.getChangedAt()).isNotNull();
        verify(persistencePort).save(traceability);
    }

    @Test
    void returnsOnlyTheLoggedCustomersOrderHistory() {
        Traceability event = new Traceability(
                "event-1", 30L, 20L, 5L, null, null, OrderStatus.PENDING, null);
        when(loggedUserPort.getUserId()).thenReturn(20L);
        when(persistencePort.findByOrderIdAndCustomerId(30L, 20L)).thenReturn(List.of(event));

        List<Traceability> result = new TraceabilityUseCase(persistencePort, loggedUserPort, ownerValidationPort)
                .getOrderTraceability(30L);

        assertThat(result).containsExactly(event);
        verify(persistencePort).findByOrderIdAndCustomerId(30L, 20L);
    }

    @Test
    void calculatesCompletedOrdersAndEmployeeRanking() {
        when(persistencePort.findByRestaurantId(5L)).thenReturn(List.of(
                event(1L, null, OrderStatus.PENDING, "2026-09-15T10:00:00Z"),
                event(1L, 9L, OrderStatus.DELIVERED, "2026-09-15T10:10:00Z"),
                event(2L, null, OrderStatus.PENDING, "2026-09-15T11:00:00Z"),
                event(2L, 9L, OrderStatus.DELIVERED, "2026-09-15T11:20:00Z"),
                event(3L, null, OrderStatus.PENDING, "2026-09-15T12:00:00Z"),
                event(3L, 8L, OrderStatus.DELIVERED, "2026-09-15T12:05:00Z"),
                event(4L, null, OrderStatus.PENDING, "2026-09-15T13:00:00Z")));
        TraceabilityUseCase useCase = new TraceabilityUseCase(
                persistencePort, loggedUserPort, ownerValidationPort);

        var orders = useCase.getOrderEfficiencies(5L);
        var ranking = useCase.getEmployeeEfficiencyRanking(5L);

        assertThat(orders).hasSize(3);
        assertThat(orders).extracting(order -> order.durationSeconds())
                .containsExactly(600L, 1200L, 300L);
        assertThat(ranking).extracting(employee -> employee.employeeId())
                .containsExactly(8L, 9L);
        assertThat(ranking.get(0).rank()).isEqualTo(1);
        assertThat(ranking.get(1).completedOrders()).isEqualTo(2L);
        assertThat(ranking.get(1).averageDurationSeconds()).isEqualTo(900D);
        verify(ownerValidationPort, org.mockito.Mockito.times(2)).validateOwnership(5L);
    }

    @Test
    void doesNotReadMetricsWhenRestaurantOwnershipFails() {
        org.mockito.Mockito.doThrow(new AuthorizationException("Access denied"))
                .when(ownerValidationPort).validateOwnership(5L);
        TraceabilityUseCase useCase = new TraceabilityUseCase(
                persistencePort, loggedUserPort, ownerValidationPort);

        assertThatThrownBy(() -> useCase.getOrderEfficiencies(5L))
                .isInstanceOf(AuthorizationException.class);
        verifyNoInteractions(persistencePort);
    }

    private Traceability event(Long orderId, Long employeeId, OrderStatus status, String changedAt) {
        return new Traceability("event-" + orderId + '-' + status, orderId, 20L, 5L, employeeId,
                null, status, Instant.parse(changedAt));
    }
}
