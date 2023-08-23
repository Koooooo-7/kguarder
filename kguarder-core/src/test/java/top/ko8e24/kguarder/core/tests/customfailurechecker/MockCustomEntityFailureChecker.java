package top.ko8e24.kguarder.core.tests.customfailurechecker;

import top.ko8e24.kguarder.core.retry.CustomFailureChecker;
import top.ko8e24.kguarder.core.support.Result;
import top.ko8e24.kguarder.core.tests.simple.fallbacker.MyFoo;

import java.util.Optional;

public class MockCustomEntityFailureChecker implements CustomFailureChecker {
    @Override
    public boolean failed(Result guardedResult) {
        final Optional<MyFoo> result = guardedResult.get();
        final Long status = result.get().getCode();
        return 200L != status;
    }
}
