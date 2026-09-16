package com.pragma.powerup.infrastructure.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pragma.powerup.domain.exception.AuthenticationException;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class LoggedUserAdapterTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void readsAValidUserIdAndRejectsInvalidAuthentication() {
        Jwt validJwt = jwt("20");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(validJwt, validJwt));
        assertThat(new LoggedUserAdapter().getUserId()).isEqualTo(20L);

        Jwt invalidJwt = jwt("customer");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(invalidJwt, invalidJwt));
        assertThatThrownBy(() -> new LoggedUserAdapter().getUserId())
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Authenticated user ID is invalid");

        SecurityContextHolder.clearContext();
        assertThatThrownBy(() -> new LoggedUserAdapter().getUserId())
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Authenticated user not found");
    }

    private Jwt jwt(String subject) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60),
                Map.of("alg", "HS256"), Map.of("sub", subject));
    }
}
