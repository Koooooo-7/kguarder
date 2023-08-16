package top.kguarder.core.tests.customfailurechecker;

import top.kguarder.core.annotation.Guarder;
import top.kguarder.core.annotation.Retry;

public class MockCustomFailureCallService {

    // a simple flag to switch result after call twice
    private boolean firstExecFlag = true;

    @Guarder(
            retry = @Retry(
                    retryTimes = 3
            ),
            failureCustomChecker = "mockCustomFailureChecker"
    )
    public Long returnSimpleCall() {
        if (firstExecFlag) {
            firstExecFlag = false;
            return 500L;
        }
        return 200L;
    }

}
