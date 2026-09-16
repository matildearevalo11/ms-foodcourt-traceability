package com.pragma.powerup.infrastructure.out.dynamodb.mapper;

import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.infrastructure.out.dynamodb.entity.TraceabilityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ITraceabilityEntityMapper {
    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "sk", ignore = true)
    TraceabilityEntity toEntity(Traceability traceability);

    Traceability toDomain(TraceabilityEntity entity);
}
