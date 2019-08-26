package com.expediagroup.beekeeper.core.messaging;

import java.util.Optional;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;
import com.expediagroup.beekeeper.core.model.Event;

public interface EventMapper<T> {
    Optional<Event<T>> map(MessageEvent messageEvent);
}
