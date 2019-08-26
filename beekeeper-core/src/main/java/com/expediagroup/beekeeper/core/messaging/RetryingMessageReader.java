package com.expediagroup.beekeeper.core.messaging;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Optional;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.amazonaws.AmazonClientException;
import com.expedia.apiary.extensions.receiver.common.messaging.MessageEvent;
import com.expedia.apiary.extensions.receiver.common.messaging.MessageReader;
import com.expedia.apiary.extensions.receiver.sqs.messaging.SqsMessageProperty;
import com.expediagroup.beekeeper.core.error.BeekeeperException;

public class RetryingMessageReader implements MessageReader {

    private static final long INITIAL_DELAY_MS = 1000;
    private static final int BACKOFF_MULTIPLIER = 2;
    private static final int MAX_ATTEMPTS = 4;
    private static final String UNKNOWN_HANDLE = "unknown";

    private final MessageReader delegate;

    public RetryingMessageReader(MessageReader delegate) {
        this.delegate = delegate;
    }

    @Override
    @Retryable(value = { AmazonClientException.class },
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = INITIAL_DELAY_MS, multiplier = BACKOFF_MULTIPLIER))
    public Optional<MessageEvent> read() {
        return delegate.read();
    }

    @Override
    @Retryable(include = { AmazonClientException.class },
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = INITIAL_DELAY_MS, multiplier = BACKOFF_MULTIPLIER))
    public void delete(MessageEvent messageEvent) {
        delegate.delete(messageEvent);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Recover
    public Optional<MessageEvent> recoverRead(AmazonClientException e) {
        throw new BeekeeperException(format("Error reading from queue after %s attempts.", MAX_ATTEMPTS), e);
    }

    @Recover
    public void recoverDelete(AmazonClientException e, MessageEvent messageEvent) {
        throw new BeekeeperException(format("Error deleting message %s from queue after %s attempts.",
                messageEvent.getMessageProperties()
                        .getOrDefault(SqsMessageProperty.SQS_MESSAGE_RECEIPT_HANDLE, UNKNOWN_HANDLE), MAX_ATTEMPTS), e);
    }
}
