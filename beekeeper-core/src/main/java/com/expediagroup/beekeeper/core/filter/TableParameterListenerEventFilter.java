package com.expediagroup.beekeeper.core.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.expedia.apiary.extensions.receiver.common.event.ListenerEvent;

@Component
public class TableParameterListenerEventFilter implements ListenerEventFilter {

    private static final String BEEKEEPER_TABLE_PARAMETER = "beekeeper.remove.unreferenced.data";

    @Override
    public boolean filter(ListenerEvent listenerEvent) {
        Map<String, String> tableParameters = listenerEvent.getTableParameters();
        if (tableParameters == null) {
            return true;
        }
        return !Boolean.valueOf(tableParameters.get(BEEKEEPER_TABLE_PARAMETER));
    }
}
