package com.pragma.powerup.application.dto.response;

import java.time.Instant;

public record OrderEfficiencyResponseDto(Long orderId, Long employeeId, Instant startedAt,
                                         Instant completedAt, Long durationSeconds) { }
