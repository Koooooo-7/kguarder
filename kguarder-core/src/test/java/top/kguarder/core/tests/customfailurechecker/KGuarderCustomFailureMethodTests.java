package top.kguarder.core.tests.customfailurechecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import top.kguarder.core.configuration.GuarderConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {GuarderConfiguration.class, KGuarderCustomFailureMethodTests.MockServicesConfiguration.class})
public class KGuarderCustomFailureMethodTests {

    @SpyBean
    private MockCustomFailureCallService mockCustomFailureCallService;

    @SpyBean
    private MockCustomFailureChecker mockCustomFailureChecker;

    @Test
    void shouldReturnResultWhenGetSuccessCodeGivenMockCustomFailureChecker() {
        final var actual = mockCustomFailureCallService.returnSimpleCall();
        Assertions.assertEquals(200, actual);
        verify(mockCustomFailureCallService, times(2)).returnSimpleCall();
        verify(mockCustomFailureChecker, times(2)).failed(any());
    }

    public static class MockServicesConfiguration {
        @Bean("mockCustomFailureChecker")
        public MockCustomFailureChecker mockCustomFailureChecker() {
            return new MockCustomFailureChecker();
        }
    }
}
