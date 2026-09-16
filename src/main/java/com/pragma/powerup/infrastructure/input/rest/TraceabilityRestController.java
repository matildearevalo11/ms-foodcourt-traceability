package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.application.handler.ITraceabilityHandler;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.infrastructure.security.RequireRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/traceability")
@RequiredArgsConstructor
@Validated
public class TraceabilityRestController {
    private final ITraceabilityHandler handler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponse<TraceabilityResponseDto> createTraceability(
            @Valid @RequestBody TraceabilityRequestDto request) {
        return new DefaultResponse<>(handler.createTraceability(request));
    }

    @GetMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequireRole(RoleEnum.CUSTOMER)
    public DefaultResponse<List<TraceabilityResponseDto>> getOrderTraceability(@PathVariable @Positive Long orderId) {
        return new DefaultResponse<>(handler.getOrderTraceability(orderId));
    }

    @GetMapping(value = "/restaurants/{restaurantId}/efficiency/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequireRole(RoleEnum.OWNER)
    public DefaultResponse<List<OrderEfficiencyResponseDto>> getOrderEfficiencies(@PathVariable @Positive Long restaurantId) {
        return new DefaultResponse<>(handler.getOrderEfficiencies(restaurantId));
    }

    @GetMapping(value = "/restaurants/{restaurantId}/efficiency/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequireRole(RoleEnum.OWNER)
    public DefaultResponse<List<EmployeeEfficiencyResponseDto>> getEmployeeEfficiencyRanking(@PathVariable @Positive Long restaurantId) {
        return new DefaultResponse<>(handler.getEmployeeEfficiencyRanking(restaurantId));
    }
}
