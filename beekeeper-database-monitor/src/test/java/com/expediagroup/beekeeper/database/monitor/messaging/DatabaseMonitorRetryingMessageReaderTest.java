package com.expediagroup.beekeeper.database.monitor.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.amazonaws.AmazonClientException;
import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;
import com.expedia.apiary.extensions.receiver.common.messaging.MessageReader;
import com.expedia.apiary.extensions.receiver.sqs.messaging.SqsMessageProperty;
import com.expediagroup.beekeeper.core.error.BeekeeperException;
import com.expediagroup.beekeeper.database.monitor.TestConfig;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { DatabaseMonitorRetryingMessageReader.class, TestConfig.class },
        loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DatabaseMonitorRetryingMessageReaderTest {

    private static final int MAX_ATTEMPTS = 4;
    private static final String HANDLE = "receiptHandle";

    @Mock
    private MessageEvent event;

    @MockBean
    @Qualifier("sqsMessageReader")
    private MessageReader delegate;

    @Autowired
    @Qualifier("databaseMonitorRetryingMessageReader")
    private MessageReader databaseMonitorRetryingMessageReader;

    @Test
    public void typicalRead() {
        when(delegate.read()).thenReturn(Optional.of(event));
        Optional<MessageEvent> read = databaseMonitorRetryingMessageReader.read();
        assertThat(read).isPresent();
        assertThat(read.get()).isEqualTo(event);
        verify(delegate).read();
    }

    @Test
    public void retryRead() {
        when(delegate.read()).thenThrow(new AmazonClientException("Exception"))
                .thenReturn(Optional.of(event));
        Optional<MessageEvent> read = databaseMonitorRetryingMessageReader.read();
        assertThat(read).isPresent();
        assertThat(read.get()).isEqualTo(event);
        verify(delegate, times(2)).read();
    }

    @Test
    public void retryMaxAttemptsRead() {
        when(delegate.read()).thenThrow(new AmazonClientException("Exception"));
        assertThatThrownBy(databaseMonitorRetryingMessageReader::read).isInstanceOf(BeekeeperException.class)
                .hasMessageContaining("Error reading from queue after " + MAX_ATTEMPTS + " attempts.");
        verify(delegate, times(MAX_ATTEMPTS)).read();
    }

    @Test
    public void typicalDelete() {
        databaseMonitorRetryingMessageReader.delete(event);
        verify(delegate).delete(event);
    }

    @Test
    public void retryDelete() {
        doThrow(new AmazonClientException("Exception")).doNothing()
                .when(delegate)
                .delete(event);
        databaseMonitorRetryingMessageReader.delete(event);
        verify(delegate, times(2)).delete(event);
    }

    @Test
    public void retryMaxAttemptsDelete() {
        when(event.getMessageProperties()).thenReturn(Map.of(SqsMessageProperty.SQS_MESSAGE_RECEIPT_HANDLE, HANDLE));
        doThrow(new AmazonClientException("Exception")).when(delegate)
                .delete(event);
        assertThatThrownBy(() -> databaseMonitorRetryingMessageReader.delete(event)).isInstanceOf(BeekeeperException.class)
                .hasMessageContaining("Error deleting message " + HANDLE + " from queue after " + MAX_ATTEMPTS + " attempts.");
        verify(delegate, times(MAX_ATTEMPTS)).delete(event);
    }

    @Test
    public void typicalClose() throws IOException {
        databaseMonitorRetryingMessageReader.close();
        verify(delegate).close();
    }
}
