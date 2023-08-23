package top.ko8e24.kguarder.core.retry;

import top.ko8e24.kguarder.core.annotation.Retry;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
@Builder
public class RetryContext {

    private long timeout;
    private TimeUnit timeoutUnit;

    private Class<? extends Exception>[] excludeEx;

    private Class<? extends Exception>[] includeEx;

    private int currentRetryTimes;
    private int retryTimes;

    private long delay;

    private TimeUnit delayTimeUnit;

    private Retry.DelayStrategy delayStrategy;

    private CustomFailureChecker customFailureChecker;

    private RetryManager retryManager;

    public void incRetryTimes() {
        this.currentRetryTimes++;
    }

    public boolean retryTimesOver() {
        return this.currentRetryTimes == retryTimes;
    }

}
