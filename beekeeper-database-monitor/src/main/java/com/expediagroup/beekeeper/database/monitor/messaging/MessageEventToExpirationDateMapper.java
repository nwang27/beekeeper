package com.expediagroup.beekeeper.database.monitor.messaging;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;
import com.expediagroup.beekeeper.core.messaging.EventMapper;
import com.expediagroup.beekeeper.core.model.EntityExpirationDate;
import com.expediagroup.beekeeper.core.model.Event;
import com.expediagroup.beekeeper.core.model.ExpirationDate;

@Component
public class MessageEventToExpirationDateMapper implements EventMapper<ExpirationDate> {
    @Override
    public Optional<Event<ExpirationDate>> map(final MessageEvent messageEvent) {

        // TODO build the actual entity
        EntityExpirationDate dummyExpDate = new EntityExpirationDate(1l, 30, "testDatabaseName", "testTableName");

        return Optional.of(new Event<>( dummyExpDate, messageEvent ));
    }
}
