package com.pragma.powerup.domain.model;

import com.pragma.powerup.domain.enums.OrderStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Traceability {
    private String id;
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private Instant changedAt;
}
