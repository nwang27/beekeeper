package com.expediagroup.beekeeper.database.monitor.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expedia.apiary.extensions.receiver.common.event.AddPartitionEvent;
import com.expedia.apiary.extensions.receiver.common.event.AlterPartitionEvent;
import com.expedia.apiary.extensions.receiver.common.event.EventType;
import com.expedia.apiary.extensions.receiver.common.event.InsertTableEvent;

@ExtendWith(MockitoExtension.class)
public class UpdateParamListenerEventFilterTest {
    @Mock
    private AddPartitionEvent addPartitionEvent;
    @Mock
    private InsertTableEvent insertTableEvent;
    @Mock
    private AlterPartitionEvent alterPartitionEvent;

    private UpdateParamListenerEventFilter listenerEventFilter = new UpdateParamListenerEventFilter();

    @Test
    public void typicalFilterCreateTableEvent() {
        when(addPartitionEvent.getEventType()).thenReturn(EventType.ADD_PARTITION);
        boolean filter = listenerEventFilter.filter(addPartitionEvent);
        assertThat(filter).isFalse();
    }

    @Test
    public void typicalFilterInsertTableEvent() {
        when(insertTableEvent.getEventType()).thenReturn(EventType.INSERT);
        boolean filter = listenerEventFilter.filter(insertTableEvent);
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
