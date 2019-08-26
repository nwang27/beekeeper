package com.expediagroup.beekeeper.core.messaging;

import java.io.Closeable;
import java.util.Optional;

import com.expediagroup.beekeeper.core.model.Event;


public interface EventReader<T> extends Closeable {

    Optional<Event<T>> read();

    void delete(Event<T> event);

}
