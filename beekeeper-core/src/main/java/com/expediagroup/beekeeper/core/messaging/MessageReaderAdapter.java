package com.expediagroup.beekeeper.core.messaging;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;
import com.expedia.apiary.extensions.receiver.common.messaging.MessageReader;
import com.expediagroup.beekeeper.core.model.Event;


public class MessageReaderAdapter<T> implements EventReader<T> {

    private static final Logger log = LoggerFactory.getLogger(MessageReaderAdapter.class);

    private final MessageReader delegate;
    private final EventMapper<T> mapper;

    public MessageReaderAdapter(MessageReader delegate, EventMapper<T> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    @Override
    public Optional<Event<T>> read() {
        Optional<MessageEvent> messageEvent = delegate.read();
        return messageEvent.flatMap(mapper::map);
    }

    @Override
    public void delete(Event<T> event) {
        try {
            delegate.delete(event.getMessageEvent());
            log.debug("Message deleted successfully");
        } catch (Exception e) {
            log.error("Could not delete message from queue: ", e);
        }
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
