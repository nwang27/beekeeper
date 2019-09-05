package com.expediagroup.beekeeper.database.monitor;

import java.util.TimeZone;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.expediagroup.beekeeper.database.monitor.app.DatabaseMonitorRunner;
import com.google.common.annotations.VisibleForTesting;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
@EnableConfigurationProperties
public class DatabaseMonitorScheduler implements ApplicationContextAware {

    private static MeterRegistry meterRegistry;
    private static ConfigurableApplicationContext context;
    private static DatabaseMonitorRunner runner;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        new SpringApplicationBuilder(DatabaseMonitorScheduler.class)
                .properties("spring.config.additional-location:classpath:/beekeeper-database-monitor-application.yml,"
                        + "${config:null}")
                .build()
                .run(args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext) applicationContext;
        runner = (DatabaseMonitorRunner) context.getBean("databaseMonitorRunner");
        meterRegistry = (MeterRegistry) context.getBean("registry");
    }

    @VisibleForTesting
    public static boolean isRunning() {
        return context != null && context.isRunning();
    }

    @VisibleForTesting
    public static void stop() {
        if (runner == null) {
            throw new RuntimeException("Application runner has not been started.");
        }
        if (context == null) {
            throw new RuntimeException("Application context has not been started.");
        }
        runner.destroy();
        context.close();
    }

    @VisibleForTesting
    public static MeterRegistry meterRegistry() {
        return meterRegistry;
    }
}
