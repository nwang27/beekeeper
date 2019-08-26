package com.expediagroup.beekeeper.database.monitor.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.apiary.extensions.receiver.common.event.EventType;
import com.expediagroup.beekeeper.core.messaging.EventReader;
import com.expediagroup.beekeeper.core.model.Event;
import com.expediagroup.beekeeper.core.model.ExpirationDate;

@Component
public class ExpirationDateUpdateService {

    private final EventReader<ExpirationDate> expirationEventReader;

    @Autowired
    public ExpirationDateUpdateService(EventReader<ExpirationDate> expirationEventReader) {
        this.expirationEventReader = expirationEventReader;
    }

    public void UpdateExpirationDate() {
        // read event
        Optional<Event<ExpirationDate>> expirationDateToBeUpdated = expirationEventReader.read();
        if (expirationDateToBeUpdated.isPresent()) {

            EventType eventType = expirationDateToBeUpdated.get().getMessageEvent().getEvent().getEventType();
            switch (eventType) {
                case CREATE_TABLE:
                    System.out.println("creating table");
                default:
                    System.out.println("updating table");
            }
            // TODO write exp date to HKAAS RDS
        }
    }

    // TODO
    public void close() throws IOException {
        expirationEventReader.close();
    }
}
