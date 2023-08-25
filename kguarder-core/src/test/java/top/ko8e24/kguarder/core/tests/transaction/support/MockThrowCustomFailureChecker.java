package top.ko8e24.kguarder.core.tests.transaction.support;

import top.ko8e24.kguarder.core.retry.CustomFailureChecker;
import top.ko8e24.kguarder.core.support.Result;

import java.util.Optional;

public class MockThrowCustomFailureChecker implements CustomFailureChecker {
    @Override
    public boolean failed(Result guardedResult) {
        throw new IllegalStateException("Mock an Exception out of transaction");
    }
}
