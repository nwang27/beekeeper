package com.expediagroup.beekeeper.database.monitor.context;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

import com.expedia.apiary.extensions.receiver.common.messaging.MessageReader;
import com.expedia.apiary.extensions.receiver.sqs.messaging.SqsMessageReader;
import com.expediagroup.beekeeper.core.filter.ListenerEventFilter;
import com.expediagroup.beekeeper.core.filter.TableParameterListenerEventFilter;
import com.expediagroup.beekeeper.core.messaging.EventReader;
import com.expediagroup.beekeeper.core.messaging.FilteringMessageReader;
import com.expediagroup.beekeeper.core.messaging.MessageReaderAdapter;
import com.expediagroup.beekeeper.core.messaging.RetryingMessageReader;
import com.expediagroup.beekeeper.core.model.ExpirationDate;
import com.expediagroup.beekeeper.database.monitor.filter.AddParamListenerEventFilter;
import com.expediagroup.beekeeper.database.monitor.messaging.MessageEventToExpirationDateMapper;

@Configuration
@ComponentScan(basePackages = { "com.expediagroup.beekeeper.core", "com.expediagroup.beekeeper.database.monitor" })
@EntityScan(basePackages = { "com.expediagroup.beekeeper.core" })
@EnableJpaRepositories(basePackages = { "com.expediagroup.beekeeper.core.repository" })
@EnableRetry(proxyTargetClass = true)
public class CommonBeans {
    @Value("https://sqs.us-west-2.amazonaws.com/229706410758/beekeeper-apiary-listener")
    private String queueUrl;

    @Bean(name = "sqsMessageReader")
    MessageReader messageReader() {
        return new SqsMessageReader.Builder(queueUrl).build();
    }

    @Bean(name = "retryingMessageReader")
    MessageReader retryingMessageReader(@Qualifier("sqsMessageReader") MessageReader messageReader) {
        return new RetryingMessageReader(messageReader);
    }

    @Bean(name = "filteringMessageReader")
    MessageReader filteringMessageReader(@Qualifier("retryingMessageReader") MessageReader messageReader,
                                         TableParameterListenerEventFilter tableParameterFilter,
                                         AddParamListenerEventFilter addParamFilter
    ) {
        List<ListenerEventFilter> filters = List.of(tableParameterFilter, addParamFilter);
        return new FilteringMessageReader(messageReader, filters);
    }

    @Bean
    EventReader<ExpirationDate> expirationDateReader(@Qualifier("filteringMessageReader") MessageReader messageReader,
                                                     MessageEventToExpirationDateMapper mapper) {
        return new MessageReaderAdapter<>(messageReader, mapper);
    }
}
