package top.kguarder.core.retry;

import top.kguarder.core.annotation.Retry;
import top.kguarder.core.component.CustomFailureChecker;
import top.kguarder.core.exception.GuarderThrowableWrapper;
import top.kguarder.core.support.ResultWrapper;
import top.kguarder.core.exception.GuarderException;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DefaultRetryManager implements RetryManager {

    public boolean failed(RetryContext context, ResultWrapper resultWrapper, CustomFailureChecker customFailureChecker) throws Throwable {
        if (customFailureChecker.failed(resultWrapper)) {
            return true;
        }

        return failed(context, resultWrapper);
    }

    @Override
    public boolean failed(RetryContext context, ResultWrapper resultWrapper) throws Throwable {
        final GuarderThrowableWrapper throwableWrapper = resultWrapper.getThrowableWrapper();
        if (Objects.isNull(throwableWrapper)) {
            return false;
        }

        final Throwable original = throwableWrapper.getOriginal();
        if (Objects.isNull(original)) {
            return false;
        }

        final boolean excludeEx = Arrays.stream(context.getExcludeEx()).anyMatch(ex -> ex.isAssignableFrom(original.getClass()));
        if (excludeEx) {
            throw original;
        }

        final boolean includeEx = Arrays.stream(context.getIncludeEx()).anyMatch(ex -> ex.isAssignableFrom(original.getClass()));
        if (includeEx) {
            return true;
        }

        throw new GuarderException("Can not handle this exception via config.", original);

    }

    @Override
    public boolean canRetry(RetryContext context, ResultWrapper resultWrapper) throws Throwable {
        if (!failed(context, resultWrapper, context.getCustomFailureChecker())) {
            resultWrapper.setSuccess(true);
            return false;
        }

        if (context.retryTimesOver()) {
            return false;
        }

        retryDelay(context);
        return true;
    }

    public void retryDelay(RetryContext context) {
        final long delay = context.getDelay();
        final Retry.DelayStrategy delayStrategy = context.getDelayStrategy();
        final TimeUnit delayTimeUnit = context.getDelayTimeUnit();
        final int totalRetryTimes = context.getRetryTimes();
        final int currentRetryTimes = context.getCurrentRetryTimes() == 0 ? 1 : context.getCurrentRetryTimes();
        final long time = delayStrategy.getDelayCalculator().calculate(delay, delayTimeUnit, totalRetryTimes, currentRetryTimes);
        sleep(time);
    }


    public void sleep(long millsTime) {
        try {
            Thread.sleep(millsTime);
        } catch (Exception ignore) {
        }
    }

}
