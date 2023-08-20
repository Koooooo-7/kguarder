package top.kguarder.core.recover;

import top.kguarder.core.support.Result;

@FunctionalInterface
public interface Fallbacker {
    Object fallback(Result guardedResult);
}
