package com.pragma.powerup.domain.model;

import java.time.Instant;

public record OrderEfficiency(Long orderId, Long employeeId, Instant startedAt,
                              Instant completedAt, Long durationSeconds) { }
