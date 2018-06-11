package com.jcr.api.statistics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Class where to define the spring configurations.
 *
 * @author jean-charles.robert
 * @since 10.06.18
 */
@Configuration
public class StatisticsConfiguration {


    @Bean
    public TaskExecutor updateExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(25);
        return executor;
    }

    @Bean
    public TaskExecutor simpleExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
