package top.kguarder.core.retry;

import top.kguarder.core.support.ResultWrapper;

public interface RetryManager {

    boolean failed(RetryContext context, ResultWrapper resultWrapper) throws Throwable;


    boolean canRetry(RetryContext context, ResultWrapper resultWrapper) throws Throwable;


}
