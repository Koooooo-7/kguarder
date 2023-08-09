package top.kguarder.core.recover;

import top.kguarder.core.support.ResultWrapper;

@FunctionalInterface
public interface Fallbacker {
    Object fallback(ResultWrapper resultWrapper);
}
