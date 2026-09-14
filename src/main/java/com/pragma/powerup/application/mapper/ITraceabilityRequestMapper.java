package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.domain.model.Traceability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ITraceabilityRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    Traceability toTraceability(TraceabilityRequestDto request);
}
