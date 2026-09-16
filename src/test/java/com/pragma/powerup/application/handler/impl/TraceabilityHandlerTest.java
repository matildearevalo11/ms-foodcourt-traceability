package com.pragma.powerup.application.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.application.mapper.ITraceabilityRequestMapper;
import com.pragma.powerup.application.mapper.ITraceabilityResponseMapper;
import com.pragma.powerup.application.mapper.IEfficiencyResponseMapper;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.enums.OrderStatus;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.model.OrderEfficiency;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceabilityHandlerTest {
    @Mock
    ITraceabilityServicePort servicePort;
    @Mock
    ITraceabilityRequestMapper requestMapper;
    @Mock
    ITraceabilityResponseMapper responseMapper;
    @Mock
    IEfficiencyResponseMapper efficiencyResponseMapper;

    @Test
    void mapsCreateAndHistoryResponses() {
        TraceabilityRequestDto request = new TraceabilityRequestDto(
                30L, 20L, 5L, null, null, OrderStatus.PENDING);
        Traceability event = new Traceability();
        TraceabilityResponseDto response = new TraceabilityResponseDto(
                "event-1", 30L, 20L, 5L, null, null, OrderStatus.PENDING, null);
        when(requestMapper.toTraceability(request)).thenReturn(event);
        when(servicePort.createTraceability(event)).thenReturn(event);
        when(responseMapper.toResponse(event)).thenReturn(response);
        when(servicePort.getOrderTraceability(30L)).thenReturn(List.of(event));
        when(responseMapper.toResponseList(List.of(event))).thenReturn(List.of(response));
        OrderEfficiency orderEfficiency = new OrderEfficiency(30L, 40L, null, null, 300L);
        OrderEfficiencyResponseDto orderResponse = new OrderEfficiencyResponseDto(30L, 40L, null, null, 300L);
        EmployeeEfficiency employeeEfficiency = new EmployeeEfficiency(1, 40L, 1L, 300D);
        EmployeeEfficiencyResponseDto employeeResponse = new EmployeeEfficiencyResponseDto(1, 40L, 1L, 300D);
        when(servicePort.getOrderEfficiencies(5L)).thenReturn(List.of(orderEfficiency));
        when(efficiencyResponseMapper.toOrderResponses(List.of(orderEfficiency))).thenReturn(List.of(orderResponse));
        when(servicePort.getEmployeeEfficiencyRanking(5L)).thenReturn(List.of(employeeEfficiency));
        when(efficiencyResponseMapper.toEmployeeResponses(List.of(employeeEfficiency)))
                .thenReturn(List.of(employeeResponse));
        TraceabilityHandler handler = new TraceabilityHandler(
                servicePort, requestMapper, responseMapper, efficiencyResponseMapper);

        assertThat(handler.createTraceability(request)).isEqualTo(response);
        assertThat(handler.getOrderTraceability(30L)).containsExactly(response);
        assertThat(handler.getOrderEfficiencies(5L)).containsExactly(orderResponse);
        assertThat(handler.getEmployeeEfficiencyRanking(5L)).containsExactly(employeeResponse);
    }
}
