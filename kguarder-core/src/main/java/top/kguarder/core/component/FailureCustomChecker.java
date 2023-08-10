package top.kguarder.core.component;

import top.kguarder.core.support.ResultWrapper;

@FunctionalInterface
public interface FailureCustomChecker {

    /*
     * Allow to custom check if current result failed or not, although it throws ex or other cases.
     */
    boolean failed(ResultWrapper resultWrapper);
}
