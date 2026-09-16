package com.pragma.powerup.application.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.application.mapper.ITraceabilityRequestMapper;
import com.pragma.powerup.application.mapper.ITraceabilityResponseMapper;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.enums.OrderStatus;
import com.pragma.powerup.domain.model.Traceability;
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
        TraceabilityHandler handler = new TraceabilityHandler(servicePort, requestMapper, responseMapper);

        assertThat(handler.createTraceability(request)).isEqualTo(response);
        assertThat(handler.getOrderTraceability(30L)).containsExactly(response);
    }
}
