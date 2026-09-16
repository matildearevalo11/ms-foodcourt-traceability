package com.pragma.powerup.infrastructure.out.rest.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.pragma.powerup.domain.exception.AuthorizationException;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OwnerValidationRestAdapterTest {
    @Test
    void validatesRestaurantOwnership() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://foodcourt");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(once(), requestTo("http://foodcourt/restaurants/5/ownership"))
                .andRespond(withNoContent());

        new OwnerValidationRestAdapter(builder.build()).validateOwnership(5L);

        server.verify();
    }

    @Test
    void translatesAuthorizationAndAvailabilityFailures() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://foodcourt");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OwnerValidationRestAdapter adapter = new OwnerValidationRestAdapter(builder.build());
        server.expect(once(), requestTo("http://foodcourt/restaurants/5/ownership"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));
        server.expect(once(), requestTo("http://foodcourt/restaurants/5/ownership"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThatThrownBy(() -> adapter.validateOwnership(5L))
                .isInstanceOf(AuthorizationException.class);

        assertThatThrownBy(() -> adapter.validateOwnership(5L))
                .isInstanceOf(ExternalServiceException.class);
        server.verify();
    }
}
