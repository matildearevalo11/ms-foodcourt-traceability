package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.ILoggedUserPort;
import com.pragma.powerup.domain.spi.IOwnerValidationPort;
import com.pragma.powerup.domain.usecase.TraceabilityUseCase;
import com.pragma.powerup.infrastructure.out.dynamodb.adapter.TraceabilityDynamoDbAdapter;
import com.pragma.powerup.infrastructure.out.dynamodb.entity.TraceabilityEntity;
import com.pragma.powerup.infrastructure.out.dynamodb.mapper.ITraceabilityEntityMapper;
import com.pragma.powerup.infrastructure.out.rest.adapter.OwnerValidationRestAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class BeanConfiguration {
    @Bean
    DynamoDbClient dynamoDbClient(@Value("${aws.region}") String region,
            @Value("${aws.credentials.profile:}") String credentialsProfile) {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider(credentialsProfile))
                .build();
    }

    @Bean
    DynamoDbTable<TraceabilityEntity> traceabilityTable(DynamoDbClient client,
            @Value("${aws.dynamodb.table-name}") String tableName) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
        return enhancedClient.table(tableName, TableSchema.fromBean(TraceabilityEntity.class));
    }

    @Bean
    ITraceabilityPersistencePort traceabilityPersistencePort(DynamoDbTable<TraceabilityEntity> table,
            ITraceabilityEntityMapper mapper) {
        return new TraceabilityDynamoDbAdapter(table, mapper);
    }

    @Bean
    ITraceabilityServicePort traceabilityServicePort(ITraceabilityPersistencePort persistencePort,
            ILoggedUserPort loggedUserPort, IOwnerValidationPort ownerValidationPort) {
        return new TraceabilityUseCase(persistencePort, loggedUserPort, ownerValidationPort);
    }

    @Bean
    RestClient foodcourtRestClient(RestClient.Builder builder,
            @Value("${clients.foodcourt.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    if (SecurityContextHolder.getContext().getAuthentication() != null
                            && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt jwt) {
                        request.getHeaders().setBearerAuth(jwt.getTokenValue());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    IOwnerValidationPort ownerValidationPort(RestClient foodcourtRestClient) {
        return new OwnerValidationRestAdapter(foodcourtRestClient);
    }

    private AwsCredentialsProvider credentialsProvider(String profile) {
        if (profile.isBlank()) {
            return DefaultCredentialsProvider.builder().build();
        }
        return ProfileCredentialsProvider.builder()
                .profileName(profile)
                .build();
    }
}
