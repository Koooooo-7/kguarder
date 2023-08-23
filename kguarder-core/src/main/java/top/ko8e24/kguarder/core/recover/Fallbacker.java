package top.ko8e24.kguarder.core.recover;

import top.ko8e24.kguarder.core.support.Result;

@FunctionalInterface
public interface Fallbacker {
    Object fallback(Result guardedResult);
}
