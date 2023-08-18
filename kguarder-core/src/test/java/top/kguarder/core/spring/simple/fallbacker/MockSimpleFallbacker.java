package top.kguarder.core.spring.simple.fallbacker;

import top.kguarder.core.exception.GuarderThrowableWrapper;
import top.kguarder.core.recover.Fallbacker;
import top.kguarder.core.support.ResultWrapper;

import java.util.List;

public class MockSimpleFallbacker implements Fallbacker {
    @Override
    public Object fallback(ResultWrapper resultWrapper) {
        final GuarderThrowableWrapper throwableWrapper = resultWrapper.getThrowableWrapper();
        final String method = throwableWrapper.getInvocation().getMethod().getName();
        return List.of("Call method", "failed", method);
    }
}
