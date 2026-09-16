package com.pragma.powerup.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.application.handler.ITraceabilityHandler;
import com.pragma.powerup.domain.enums.OrderStatus;
import com.pragma.powerup.infrastructure.configuration.WebConfiguration;
import com.pragma.powerup.infrastructure.configuration.SecurityConfiguration;
import com.pragma.powerup.infrastructure.exceptionhandler.ControllerAdvisor;
import com.pragma.powerup.infrastructure.security.RoleAuthorizationInterceptor;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TraceabilityRestController.class)
@Import({ControllerAdvisor.class, WebConfiguration.class, SecurityConfiguration.class,
        RoleAuthorizationInterceptor.class})
@TestPropertySource(properties = "internal.api-key=test-api-key")
class TraceabilityRestControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    ITraceabilityHandler handler;

    @Test
    void registersTraceabilityWithInternalCredential() throws Exception {
        when(handler.createTraceability(any())).thenReturn(new TraceabilityResponseDto(
                "event-1", 30L, 20L, 5L, 40L, OrderStatus.PENDING, OrderStatus.IN_PREPARATION,
                Instant.parse("2026-09-13T12:00:00Z")));

        mvc.perform(post("/traceability")
                        .header("X-Internal-Api-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.employeeId").value(40))
                .andExpect(jsonPath("$.data.newStatus").value("IN_PREPARATION"));
    }

    @Test
    void rejectsMissingInternalCredentialAndInvalidRequest() throws Exception {
        mvc.perform(post("/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isForbidden());

        mvc.perform(post("/traceability")
                        .header("X-Internal-Api-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsTheAuthenticatedCustomersOrderHistory() throws Exception {
        when(handler.getOrderTraceability(30L)).thenReturn(List.of(new TraceabilityResponseDto(
                "event-1", 30L, 20L, 5L, null, null, OrderStatus.PENDING,
                Instant.parse("2026-09-13T12:00:00Z"))));

        mvc.perform(get("/traceability/orders/30")
                        .with(jwt().jwt(token -> token.subject("20").claim("role", "CUSTOMER"))
                                .authorities(createAuthorityList("ROLE_CUSTOMER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderId").value(30))
                .andExpect(jsonPath("$.data[0].newStatus").value("PENDING"));
    }

    @Test
    void rejectsNonCustomerRole() throws Exception {
        mvc.perform(get("/traceability/orders/30")
                        .with(jwt().jwt(token -> token.subject("40").claim("role", "EMPLOYEE"))
                                .authorities(createAuthorityList("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    private String validBody() {
        return """
                {"orderId":30,"customerId":20,"restaurantId":5,"employeeId":40,
                "previousStatus":"PENDING","newStatus":"IN_PREPARATION"}
                """;
    }
}
