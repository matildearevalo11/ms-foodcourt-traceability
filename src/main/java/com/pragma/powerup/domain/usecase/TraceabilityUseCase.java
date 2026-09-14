package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceabilityUseCase implements ITraceabilityServicePort {
    private final ITraceabilityPersistencePort persistencePort;

    @Override
    public Traceability createTraceability(Traceability traceability) {
        traceability.setId(UUID.randomUUID().toString());
        traceability.setChangedAt(Instant.now());
        return persistencePort.save(traceability);
    }
}
