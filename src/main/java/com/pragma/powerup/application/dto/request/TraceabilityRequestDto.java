package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TraceabilityRequestDto(
        @NotNull(message = "Order id is required") @Positive(message = "Order id must be positive") Long orderId,
        @NotNull(message = "Customer id is required") @Positive(message = "Customer id must be positive") Long customerId,
        @NotNull(message = "Restaurant id is required") @Positive(message = "Restaurant id must be positive") Long restaurantId,
        @Positive(message = "Employee id must be positive") Long employeeId,
        OrderStatus previousStatus,
        @NotNull(message = "New status is required") OrderStatus newStatus
) { }
