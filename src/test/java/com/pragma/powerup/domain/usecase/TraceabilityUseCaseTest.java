package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.enums.OrderStatus;
import com.pragma.powerup.domain.model.Traceability;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceabilityUseCaseTest {
    @Mock
    ITraceabilityPersistencePort persistencePort;

    @Test
    void assignsIdentityAndTimestampBeforeSaving() {
        Traceability traceability = new Traceability(
                null, 30L, 20L, 5L, null, null, OrderStatus.PENDING, null);
        when(persistencePort.save(traceability)).thenReturn(traceability);

        Traceability result = new TraceabilityUseCase(persistencePort).createTraceability(traceability);

        assertThat(result.getId()).isNotBlank();
        assertThat(result.getChangedAt()).isNotNull();
        verify(persistencePort).save(traceability);
    }
}
