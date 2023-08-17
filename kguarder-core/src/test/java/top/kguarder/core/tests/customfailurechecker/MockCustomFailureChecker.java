package top.kguarder.core.tests.customfailurechecker;

import top.kguarder.core.retry.CustomFailureChecker;
import top.kguarder.core.support.ResultWrapper;

import java.util.Optional;

public class MockCustomFailureChecker implements CustomFailureChecker {
    @Override
    public boolean failed(ResultWrapper resultWrapper) {
        final Optional<Long> result = resultWrapper.getResult();
        final Long status = result.orElse(0L);
        return 200L != status;
    }
}
