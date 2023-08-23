package top.ko8e24.kguarder.core.retry;

import top.ko8e24.kguarder.core.support.Result;

@FunctionalInterface
public interface CustomFailureChecker {

    /*
     * Allow to custom check if current result failed or not, although it throws ex or other cases.
     */
    boolean failed(Result guardedResult);
}
