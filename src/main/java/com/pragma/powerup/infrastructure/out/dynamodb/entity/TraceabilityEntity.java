package com.pragma.powerup.infrastructure.out.dynamodb.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
public class TraceabilityEntity {
    private String pk;
    private String sk;
    private String id;
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private Long employeeId;
    private String previousStatus;
    private String newStatus;
    private Instant changedAt;

    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "restaurant-changed-at-index")
    public Long getRestaurantId() {
        return restaurantId;
    }

    @DynamoDbSecondarySortKey(indexNames = "restaurant-changed-at-index")
    public Instant getChangedAt() {
        return changedAt;
    }
}
