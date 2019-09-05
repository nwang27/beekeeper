package com.expediagroup.beekeeper.database.monitor.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import com.expediagroup.beekeeper.core.error.BeekeeperException;
import com.expediagroup.beekeeper.database.monitor.service.ExpirationDateUpdateService;

@ExtendWith(MockitoExtension.class)
public class DatabaseMonitorRunnerTest {

    @Mock
    private ApplicationArguments args;

    @Mock
    private ExpirationDateUpdateService expirationDateUpdateService;

    private DatabaseMonitorRunner databaseMonitorRunner;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @BeforeEach
    public void init() {
        databaseMonitorRunner = new DatabaseMonitorRunner(expirationDateUpdateService);
    }

    @Test
    public void typicalRun() throws Exception {
        runRunner();
        await().atMost(Duration.FIVE_SECONDS).
                untilAsserted(() -> verify(expirationDateUpdateService, atLeast(1)).updateExpirationDate());
        destroy();
        verify(expirationDateUpdateService).close();
    }

    @Test
    public void typicalRunWithException() throws Exception {
        doThrow(new RuntimeException())
                .doNothing()
                .when(expirationDateUpdateService)
                .updateExpirationDate();
        runRunner();
        await().atMost(Duration.FIVE_SECONDS)
                .untilAsserted(() -> verify(expirationDateUpdateService, atLeast(2)).updateExpirationDate());
        destroy();;
        verify(expirationDateUpdateService).close();
    }

    @Test
    public void typicalRunSchedulerTimeoutOnDestroy() throws Exception {
        doAnswer(answer -> {
            Thread.sleep(15000L);
            return null;
        }).when(expirationDateUpdateService)
                .updateExpirationDate();

        try {
            runRunner();
            await().atMost(Duration.FIVE_SECONDS)
                    .untilAsserted(() -> verify(expirationDateUpdateService, atLeast(1)).updateExpirationDate());
            destroy();
            fail("Runner should have thrown exception");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(BeekeeperException.class);
            assertThat(e.getMessage()).isEqualTo("Runner taking too long to shut down");
            verify(expirationDateUpdateService).close();
        }
    }




    private void runRunner() {
        executor.execute(() -> {
            try {
                databaseMonitorRunner.run(args);
            }catch (Exception e) {
                fail("exception thrown on run");
            }
        });
    }

    private void destroy() throws InterruptedException {
        databaseMonitorRunner.destroy();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }
}
