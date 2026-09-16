package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.application.handler.ITraceabilityHandler;
import com.pragma.powerup.application.mapper.ITraceabilityRequestMapper;
import com.pragma.powerup.application.mapper.ITraceabilityResponseMapper;
import com.pragma.powerup.application.mapper.IEfficiencyResponseMapper;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraceabilityHandler implements ITraceabilityHandler {
    private final ITraceabilityServicePort servicePort;
    private final ITraceabilityRequestMapper requestMapper;
    private final ITraceabilityResponseMapper responseMapper;
    private final IEfficiencyResponseMapper efficiencyResponseMapper;

    @Override
    public TraceabilityResponseDto createTraceability(TraceabilityRequestDto request) {
        return responseMapper.toResponse(servicePort.createTraceability(requestMapper.toTraceability(request)));
    }

    @Override
    public List<TraceabilityResponseDto> getOrderTraceability(Long orderId) {
        return responseMapper.toResponseList(servicePort.getOrderTraceability(orderId));
    }

    @Override
    public List<OrderEfficiencyResponseDto> getOrderEfficiencies(Long restaurantId) {
        return efficiencyResponseMapper.toOrderResponses(servicePort.getOrderEfficiencies(restaurantId));
    }

    @Override
    public List<EmployeeEfficiencyResponseDto> getEmployeeEfficiencyRanking(Long restaurantId) {
        return efficiencyResponseMapper.toEmployeeResponses(servicePort.getEmployeeEfficiencyRanking(restaurantId));
    }
}
