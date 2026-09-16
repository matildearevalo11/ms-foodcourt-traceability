package com.pragma.powerup.infrastructure.out.rest.adapter;

import com.pragma.powerup.domain.exception.AuthorizationException;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.spi.IOwnerValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@RequiredArgsConstructor
public class OwnerValidationRestAdapter implements IOwnerValidationPort {
    private final RestClient foodcourtRestClient;

    @Override
    public void validateOwnership(Long restaurantId) {
        try {
            foodcourtRestClient.get()
                    .uri("/restaurants/{restaurantId}/ownership", restaurantId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new AuthorizationException(ExceptionMessages.ACCESS_DENIED.getMessage());
            }
            throw unavailable();
        } catch (RestClientException exception) {
            throw unavailable();
        }
    }

    private ExternalServiceException unavailable() {
        return new ExternalServiceException(ExceptionMessages.FOODCOURT_SERVICE_UNAVAILABLE.getMessage());
    }
}
