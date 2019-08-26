package com.expediagroup.beekeeper.core.messaging;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;
import com.expedia.apiary.extensions.receiver.common.messaging.MessageReader;
import com.expediagroup.beekeeper.core.filter.ListenerEventFilter;

public class FilteringMessageReader implements MessageReader {

    private final MessageReader delegate;
    private final List<ListenerEventFilter> filters;

    public FilteringMessageReader(MessageReader delegate, List<ListenerEventFilter> filters) {
        this.delegate = delegate;
        this.filters = filters;
    }

    @Override
    public Optional<MessageEvent> read() {
        Optional<MessageEvent> messageEvent = delegate.read();
        if (messageEvent.isEmpty()) {
            return Optional.empty();
        }
        boolean isFiltered = filters.stream()
                .anyMatch(filter -> filter.filter(messageEvent.get().getEvent()));
        if (isFiltered) {
            delete(messageEvent.get());
            return Optional.empty();
        }
        return messageEvent;
    }

    @Override
    public void delete(MessageEvent messageEvent) {
        delegate.delete(messageEvent);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
