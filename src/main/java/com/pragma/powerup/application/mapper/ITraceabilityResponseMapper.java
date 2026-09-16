package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.domain.model.Traceability;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ITraceabilityResponseMapper {
    TraceabilityResponseDto toResponse(Traceability traceability);

    List<TraceabilityResponseDto> toResponseList(List<Traceability> traceability);
}
