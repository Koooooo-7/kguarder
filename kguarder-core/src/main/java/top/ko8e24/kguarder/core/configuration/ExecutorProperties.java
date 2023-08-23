package top.ko8e24.kguarder.core.configuration;

import lombok.Data;

import java.time.Duration;

@Data
public class ExecutorProperties {
    private int corePoolSize = 5;
    private int maximumPoolSize = 10;
    private Duration keepAliveDuration = Duration.ofMinutes(20);
    private int workQueueSize = 2000;

}
