package com.pragma.powerup.domain.model;

public record EmployeeEfficiency(Integer rank, Long employeeId, Long completedOrders,
                                 Double averageDurationSeconds) { }
