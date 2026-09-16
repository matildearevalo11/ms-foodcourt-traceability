package com.pragma.powerup.domain.spi;

public interface IOwnerValidationPort {
    void validateOwnership(Long restaurantId);
}
