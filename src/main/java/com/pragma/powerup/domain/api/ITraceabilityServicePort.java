package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.model.OrderEfficiency;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import java.util.List;

public interface ITraceabilityServicePort {
    Traceability createTraceability(Traceability traceability);

    List<Traceability> getOrderTraceability(Long orderId);

    List<OrderEfficiency> getOrderEfficiencies(Long restaurantId);

    List<EmployeeEfficiency> getEmployeeEfficiencyRanking(Long restaurantId);
}
