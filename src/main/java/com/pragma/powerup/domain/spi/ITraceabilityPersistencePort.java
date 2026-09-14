package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Traceability;

public interface ITraceabilityPersistencePort {
    Traceability save(Traceability traceability);
}
