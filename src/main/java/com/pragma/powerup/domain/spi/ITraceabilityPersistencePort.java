package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Traceability;
import java.util.List;

public interface ITraceabilityPersistencePort {
    Traceability save(Traceability traceability);

    List<Traceability> findByOrderIdAndCustomerId(Long orderId, Long customerId);
}
