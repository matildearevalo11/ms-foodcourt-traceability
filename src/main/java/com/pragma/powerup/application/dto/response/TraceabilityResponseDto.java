package com.pragma.powerup.application.dto.response;

import com.pragma.powerup.domain.enums.OrderStatus;
import java.time.Instant;

public record TraceabilityResponseDto(String id, Long orderId, Long customerId, Long restaurantId,
        OrderStatus previousStatus, OrderStatus newStatus, Instant changedAt) { }
