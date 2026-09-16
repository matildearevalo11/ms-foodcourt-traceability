package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import com.pragma.powerup.domain.model.OrderEfficiency;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IEfficiencyResponseMapper {
    List<OrderEfficiencyResponseDto> toOrderResponses(List<OrderEfficiency> efficiencies);

    List<EmployeeEfficiencyResponseDto> toEmployeeResponses(List<EmployeeEfficiency> efficiencies);
}
