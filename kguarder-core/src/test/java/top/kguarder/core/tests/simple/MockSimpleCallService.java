package top.kguarder.core.tests.simple;

import top.kguarder.core.annotation.Guarder;
import top.kguarder.core.annotation.Recover;
import top.kguarder.core.annotation.Retry;

public class MockSimpleCallService {

    private boolean firstExFlag = true;

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

}
