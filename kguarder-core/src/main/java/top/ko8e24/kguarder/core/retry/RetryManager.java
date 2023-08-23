package top.ko8e24.kguarder.core.retry;

import top.ko8e24.kguarder.core.support.GuardedResult;

public interface RetryManager {

    boolean failed(RetryContext context, GuardedResult guardedResult) throws Throwable;


    boolean canRetry(RetryContext context, GuardedResult guardedResult) throws Throwable;


}
