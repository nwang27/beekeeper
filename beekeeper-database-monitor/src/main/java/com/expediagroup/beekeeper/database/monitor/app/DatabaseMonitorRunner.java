package com.expediagroup.beekeeper.database.monitor.app;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.expediagroup.beekeeper.core.error.BeekeeperException;
import com.expediagroup.beekeeper.database.monitor.service.ExpirationDateUpdateService;

@Component
public class DatabaseMonitorRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMonitorRunner.class);
    private static final long RUNNER_DESTROY_TIMEOUT_SECONDS = 11L;

    private final ReentrantLock lock;
    private final ExpirationDateUpdateService expirationDateUpdateService;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public DatabaseMonitorRunner(ExpirationDateUpdateService expirationDateUpdateService) {
        this.expirationDateUpdateService = expirationDateUpdateService;
        lock = new ReentrantLock();
    }

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        lock.lock();
        running.set(true);
        log.info("Starting application runner");
        while (running.get()) {
            try {
                expirationDateUpdateService.updateExpirationDate();
            } catch (Exception e) {
                log.error("Error while updating expiration date", e);
            }
        }
        log.info("Runner has stopped");
        lock.unlock();
    }

    @PreDestroy
    public void destroy() {
        try {
            log.info("Shutting down runner");
            running.set(false);
            if (!lock.tryLock(RUNNER_DESTROY_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new BeekeeperException("Runner taking too long to shut down");
            }
        } catch (InterruptedException e) {
            throw new BeekeeperException("Runner taking too long to shut down", e);
        } finally {
            try {
                expirationDateUpdateService.close();
            } catch (IOException e) {
                throw new BeekeeperException("Problem closing resources", e);
            }
        }
        log.info("Runner is shutdown");
    }
}
