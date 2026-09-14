package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Traceability;

public interface ITraceabilityServicePort {
    Traceability createTraceability(Traceability traceability);
}
