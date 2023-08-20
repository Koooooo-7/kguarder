package top.kguarder.core.retry;

import top.kguarder.core.support.GuardedResult;

public interface RetryManager {

    boolean failed(RetryContext context, GuardedResult guardedResult) throws Throwable;


    boolean canRetry(RetryContext context, GuardedResult guardedResult) throws Throwable;


}
