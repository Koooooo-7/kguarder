package top.kguarder.core.retry;

import top.kguarder.core.support.Result;

@FunctionalInterface
public interface CustomFailureChecker {

    /*
     * Allow to custom check if current result failed or not, although it throws ex or other cases.
     */
    boolean failed(Result guardedResult);
}
