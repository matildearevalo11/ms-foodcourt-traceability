package com.pragma.powerup.infrastructure.out.dynamodb.adapter;

import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.infrastructure.out.dynamodb.entity.TraceabilityEntity;
import com.pragma.powerup.infrastructure.out.dynamodb.mapper.ITraceabilityEntityMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@RequiredArgsConstructor
public class TraceabilityDynamoDbAdapter implements ITraceabilityPersistencePort {
    private static final String ORDER_PREFIX = "ORDER#";
    private static final String EVENT_PREFIX = "EVENT#";

    private final DynamoDbTable<TraceabilityEntity> table;
    private final ITraceabilityEntityMapper mapper;

    @Override
    public Traceability save(Traceability traceability) {
        try {
            TraceabilityEntity entity = mapper.toEntity(traceability);
            entity.setPk(ORDER_PREFIX + traceability.getOrderId());
            entity.setSk(EVENT_PREFIX + traceability.getChangedAt() + "#" + traceability.getId());
            table.putItem(entity);
            return mapper.toDomain(entity);
        } catch (DynamoDbException exception) {
            throw new ExternalServiceException(ExceptionMessages.TRACEABILITY_PERSISTENCE_ERROR.getMessage());
        }
    }

    @Override
    public List<Traceability> findByOrderIdAndCustomerId(Long orderId, Long customerId) {
        try {
            QueryConditional orderKey = QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(ORDER_PREFIX + orderId).build());
            return table.query(orderKey).items().stream()
                    .filter(entity -> customerId.equals(entity.getCustomerId()))
                    .map(mapper::toDomain)
                    .toList();
        } catch (DynamoDbException exception) {
            throw new ExternalServiceException(ExceptionMessages.TRACEABILITY_PERSISTENCE_ERROR.getMessage());
        }
    }
}
