package com.expediagroup.beekeeper.database.monitor.service;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.apiary.extensions.receiver.common.event.EventType;
import com.expediagroup.beekeeper.core.messaging.EventReader;
import com.expediagroup.beekeeper.core.model.Event;
import com.expediagroup.beekeeper.core.model.ExpirationDate;

@Component
public class ExpirationDateUpdateService {

    private static final Logger log = LoggerFactory.getLogger(ExpirationDateUpdateService.class);

    private final EventReader<ExpirationDate> expirationEventReader;

    @Autowired
    public ExpirationDateUpdateService(EventReader<ExpirationDate> expirationEventReader) {
        this.expirationEventReader = expirationEventReader;
    }

    public void updateExpirationDate() {
        Optional<Event<ExpirationDate>> expirationDateToBeUpdated = expirationEventReader.read();
        if (expirationDateToBeUpdated.isPresent()) {
            EventType eventType = expirationDateToBeUpdated.get().getMessageEvent().getEvent().getEventType();
            switch (eventType) {
                case CREATE_TABLE:
                    log.info("creating table");
                    break;
                default:
                    log.info("updating table");
            }
            // TODO write exp date to HKAAS RDS
            this.expirationEventReader.delete(expirationDateToBeUpdated.get());
        }
    }

    // TODO
    public void close() throws IOException {
        expirationEventReader.close();
    }
}
