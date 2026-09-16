package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.ILoggedUserPort;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceabilityUseCase implements ITraceabilityServicePort {
    private final ITraceabilityPersistencePort persistencePort;
    private final ILoggedUserPort loggedUserPort;

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
}
