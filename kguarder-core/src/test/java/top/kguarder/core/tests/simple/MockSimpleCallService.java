package top.kguarder.core.tests.simple;

import top.kguarder.core.annotation.Guarder;
import top.kguarder.core.annotation.Recover;
import top.kguarder.core.annotation.Retry;

public class MockSimpleCallService {

    // a simple flag to switch result after call twice
    private boolean firstExFlag = true;

    @Guarder(
            retry = @Retry(
                    retryTimes = 3
            )
    )
    public Long returnCall() {
        return 200L;
    }

    @Guarder(
            excludeEx = Exception.class,
            retry = @Retry(
                    retryTimes = 3
            )
    )
    public void returnCallEx() {
        throw new IllegalStateException("Mock an Exception");
    }

    @Guarder(
            retry = @Retry(
                    retryTimes = 3
            )
    )
    public Long returnSimpleCall() {
        if (firstExFlag) {
            firstExFlag = false;
            throw new IllegalStateException("Mock an Exception");
        }
        return 200L;
    }

    @Guarder(
            retry = @Retry(
                    retryTimes = 3
            ),
            recover = @Recover(
                    fallback = "mockSimpleFallbacker"
            )

    )
    public Object returnSimpleObjectRecoverCall() {
        throw new IllegalStateException("Mock an Exception");
    }

    @Guarder(
            excludeEx = {IllegalStateException.class},
            retry = @Retry(
                    retryTimes = 3
            ),
            recover = @Recover(
                    fallback = "mockSimpleFallbacker"
            )

    )
    public Object throwExceptionMatchExcludeExCall() {
        throw new IllegalStateException("Mock an Exception");
    }

    @Guarder(
            timeout = 2L,
            recover = @Recover(
                    fallback = "mockSimpleFallbacker"
            )

    )
    public Object returnSimpleObjectRecoverCallWithTimeout() {

        try {
            Thread.sleep(3_000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
