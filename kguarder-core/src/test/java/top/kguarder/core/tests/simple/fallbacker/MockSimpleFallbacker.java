package top.kguarder.core.tests.simple.fallbacker;

import top.kguarder.core.recover.Fallbacker;
import top.kguarder.core.support.Result;

import java.util.List;

public class MockSimpleFallbacker implements Fallbacker {
    @Override
    public Object fallback(Result guardedResult) {
        final var target = guardedResult.getTarget();
        final String method = target.getMethod().getName();
        return List.of("Call method", "failed", method);
    }
}
