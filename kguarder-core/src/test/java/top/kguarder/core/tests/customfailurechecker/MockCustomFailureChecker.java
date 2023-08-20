package top.kguarder.core.tests.customfailurechecker;

import top.kguarder.core.retry.CustomFailureChecker;
import top.kguarder.core.support.Result;

import java.util.Optional;

public class MockCustomFailureChecker implements CustomFailureChecker {
    @Override
    public boolean failed(Result guardedResult) {
        final Optional<Long> result = guardedResult.get();
        final Long status = result.orElse(0L);
        return 200L != status;
    }
}
