package top.kguarder.core.tests.customfailurechecker;

import top.kguarder.core.retry.CustomFailureChecker;
import top.kguarder.core.support.Result;
import top.kguarder.core.tests.simple.fallbacker.MyFoo;

import java.util.Optional;

public class MockCustomEntityFailureChecker implements CustomFailureChecker {
    @Override
    public boolean failed(Result guardedResult) {
        final Optional<MyFoo> result = guardedResult.get();
        final Long status = result.get().getCode();
        return 200L != status;
    }
}
