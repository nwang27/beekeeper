package com.expediagroup.beekeeper.database.monitor.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expedia.apiary.extensions.receiver.common.event.AlterPartitionEvent;
import com.expedia.apiary.extensions.receiver.common.event.CreateTableEvent;
import com.expedia.apiary.extensions.receiver.common.event.EventType;

@ExtendWith(MockitoExtension.class)
public class AddParamListenerEventFilterTest {
    @Mock
    private CreateTableEvent createTableEvent;
    @Mock
    private AlterPartitionEvent alterPartitionEvent;

    private AddParamListenerEventFilter listenerEventFilter = new AddParamListenerEventFilter();

    @Test
    public void typicalFilterCreateTableEvent() {
        when(createTableEvent.getEventType()).thenReturn(EventType.CREATE_TABLE);
        boolean filter = listenerEventFilter.filter(createTableEvent);
        assertThat(filter).isFalse();
    }

    @Test
    public void typicalFilterAlterPartitionEvent() {
        when(alterPartitionEvent.getEventType()).thenReturn(EventType.ALTER_PARTITION);
        boolean filter = listenerEventFilter.filter(alterPartitionEvent);
        assertThat(filter).isTrue();
    }

    @Test
    public void typicalFilterNullEvent() {
        boolean filter = listenerEventFilter.filter(null);
        assertThat(filter).isTrue();
    }
}
