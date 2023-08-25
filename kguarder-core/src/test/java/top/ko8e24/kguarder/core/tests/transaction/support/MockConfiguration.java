package top.ko8e24.kguarder.core.tests.transaction.support;

import org.springframework.context.annotation.Bean;

public class MockConfiguration {

    @Bean("mockThrowCustomFailureChecker")
    public MockThrowCustomFailureChecker mockThrowCustomFailureChecker() {
        return new MockThrowCustomFailureChecker();
    }

}
