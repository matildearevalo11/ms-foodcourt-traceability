package com.pragma.powerup.application.dto.response;

public record EmployeeEfficiencyResponseDto(Integer rank, Long employeeId, Long completedOrders,
                                            Double averageDurationSeconds) { }
