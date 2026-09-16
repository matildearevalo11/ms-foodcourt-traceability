package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import java.util.List;

public interface ITraceabilityHandler {
    TraceabilityResponseDto createTraceability(TraceabilityRequestDto request);

    List<TraceabilityResponseDto> getOrderTraceability(Long orderId);
}
