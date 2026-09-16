package com.pragma.powerup.infrastructure.out.dynamodb.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.pragma.powerup.domain.enums.OrderStatus;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.infrastructure.out.dynamodb.entity.TraceabilityEntity;
import com.pragma.powerup.infrastructure.out.dynamodb.mapper.ITraceabilityEntityMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@ExtendWith(MockitoExtension.class)
class TraceabilityDynamoDbAdapterTest {
    @Mock
    DynamoDbTable<TraceabilityEntity> table;
    @Mock
    ITraceabilityEntityMapper mapper;

    @Test
    void savesEventUsingOrderAndChronologicalKeys() {
        Traceability traceability = traceability();
        TraceabilityEntity entity = new TraceabilityEntity();
        when(mapper.toEntity(traceability)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(traceability);

        Traceability result = new TraceabilityDynamoDbAdapter(table, mapper).save(traceability);

        assertThat(result).isSameAs(traceability);
        assertThat(entity.getPk()).isEqualTo("ORDER#30");
        assertThat(entity.getSk()).startsWith("EVENT#2026-09-13T12:00:00Z#event-1");
        verify(table).putItem(entity);
    }

    @Test
    void translatesDynamoDbFailure() {
        Traceability traceability = traceability();
        TraceabilityEntity entity = new TraceabilityEntity();
        when(mapper.toEntity(traceability)).thenReturn(entity);
        doThrow(DynamoDbException.builder().message("unavailable").build()).when(table).putItem(entity);

        assertThatThrownBy(() -> new TraceabilityDynamoDbAdapter(table, mapper).save(traceability))
                .isInstanceOf(ExternalServiceException.class);
    }

    @Test
    void returnsChronologicalEventsOnlyForTheRequestedCustomer() {
        TraceabilityEntity ownFirst = entity("event-1", 20L);
        TraceabilityEntity foreign = entity("event-2", 99L);
        TraceabilityEntity ownLast = entity("event-3", 20L);
        PageIterable<TraceabilityEntity> pages = org.mockito.Mockito.mock(PageIterable.class);
        SdkIterable<TraceabilityEntity> items = () -> List.of(ownFirst, foreign, ownLast).iterator();
        when(table.query(any(QueryConditional.class))).thenReturn(pages);
        when(pages.items()).thenReturn(items);
        when(mapper.toDomain(ownFirst)).thenReturn(traceability("event-1"));
        when(mapper.toDomain(ownLast)).thenReturn(traceability("event-3"));

        List<Traceability> result = new TraceabilityDynamoDbAdapter(table, mapper)
                .findByOrderIdAndCustomerId(30L, 20L);

        assertThat(result).extracting(Traceability::getId).containsExactly("event-1", "event-3");
    }

    private Traceability traceability() {
        return traceability("event-1");
    }

    private Traceability traceability(String id) {
        return new Traceability(id, 30L, 20L, 5L, 40L,
                null, OrderStatus.PENDING, Instant.parse("2026-09-13T12:00:00Z"));
    }

    private TraceabilityEntity entity(String id, Long customerId) {
        TraceabilityEntity entity = new TraceabilityEntity();
        entity.setId(id);
        entity.setCustomerId(customerId);
        return entity;
    }
}
