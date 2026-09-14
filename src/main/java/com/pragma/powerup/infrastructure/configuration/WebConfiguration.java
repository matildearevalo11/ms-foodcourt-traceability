package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.infrastructure.security.InternalApiKeyInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final InternalApiKeyInterceptor internalApiKeyInterceptor;

    public WebConfiguration(@Value("${internal.api-key}") String apiKey) {
        this.internalApiKeyInterceptor = new InternalApiKeyInterceptor(apiKey);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalApiKeyInterceptor)
                .addPathPatterns("/traceability/**");
    }
}
