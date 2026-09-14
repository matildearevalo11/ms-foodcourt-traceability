package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.exception.AuthorizationException;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.web.servlet.HandlerInterceptor;

public class InternalApiKeyInterceptor implements HandlerInterceptor {
    private static final String API_KEY_HEADER = "X-Internal-Api-Key";

    private final byte[] expectedApiKey;

    public InternalApiKeyInterceptor(String expectedApiKey) {
        this.expectedApiKey = expectedApiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String suppliedApiKey = request.getHeader(API_KEY_HEADER);
        boolean valid = suppliedApiKey != null && MessageDigest.isEqual(
                expectedApiKey, suppliedApiKey.getBytes(StandardCharsets.UTF_8));
        if (!valid) {
            throw new AuthorizationException(ExceptionMessages.INVALID_INTERNAL_API_KEY.getMessage());
        }
        return true;
    }
}
