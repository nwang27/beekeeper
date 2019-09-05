package com.expediagroup.beekeeper.database.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageReader;
import com.expedia.apiary.extensions.receiver.sqs.messaging.SqsMessageReader;
import com.expediagroup.beekeeper.database.monitor.messaging.DatabaseMonitorRetryingMessageReader;

@Configuration
@EnableRetry(proxyTargetClass=true)
public class TestConfig {

    private static final String URL = "url";

    @Bean
    public SqsMessageReader sqsMessageReader() {
        return new SqsMessageReader.Builder(URL).build();
    }

    @Bean
    public DatabaseMonitorRetryingMessageReader databaseMonitorRetryingMessageReader(MessageReader messageReader) {
        return new DatabaseMonitorRetryingMessageReader(messageReader);
    }
}
