package com.expediagroup.beekeeper.database.monitor.filter;

import com.expedia.apiary.extensions.receiver.common.event.AddPartitionEvent;
import com.expedia.apiary.extensions.receiver.common.event.InsertTableEvent;
import com.expedia.apiary.extensions.receiver.common.event.ListenerEvent;
import com.expediagroup.beekeeper.core.filter.ListenerEventFilter;

public class UpdateParamListenerEventFilter implements ListenerEventFilter {

    @Override
    public boolean filter(final ListenerEvent listenerEvent) {
        if (listenerEvent == null) {
            return true;
        }
        Class<? extends ListenerEvent> eventClass = listenerEvent.getEventType().eventClass();
        return !(AddPartitionEvent.class.equals(eventClass) ||
                InsertTableEvent.class.equals(eventClass));
    }
}
