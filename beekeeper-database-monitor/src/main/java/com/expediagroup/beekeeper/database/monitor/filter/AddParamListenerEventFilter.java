package com.expediagroup.beekeeper.database.monitor.filter;

import org.springframework.stereotype.Component;

import com.expedia.apiary.extensions.receiver.common.event.CreateTableEvent;
import com.expedia.apiary.extensions.receiver.common.event.ListenerEvent;
import com.expediagroup.beekeeper.core.filter.ListenerEventFilter;

@Component
public class AddParamListenerEventFilter implements ListenerEventFilter {

    @Override
    public boolean filter(ListenerEvent listenerEvent) {
        if (listenerEvent == null) {
            return true;
        }
        Class<? extends ListenerEvent> eventClass = listenerEvent.getEventType().eventClass();
        return !CreateTableEvent.class.equals(eventClass);
    }
}
