package com.expediagroup.beekeeper.core.model;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;

public class Event<T> {
    private final T eventEntity;
    private final MessageEvent messageEvent;

    public Event(T eventEntity, MessageEvent messageEvent) {
        this.eventEntity = eventEntity;
        this.messageEvent = messageEvent;
    }

    public T getEventEntity() {
        return eventEntity;
    }

    public MessageEvent getMessageEvent() {
        return messageEvent;
    }
}

