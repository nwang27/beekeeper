package com.expediagroup.beekeeper.core.filter;

import com.expedia.apiary.extensions.receiver.common.event.ListenerEvent;

public interface ListenerEventFilter {
    boolean filter(ListenerEvent listenerEvent);
}
