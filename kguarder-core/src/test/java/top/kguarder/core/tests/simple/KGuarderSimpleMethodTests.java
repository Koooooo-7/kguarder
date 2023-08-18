package top.kguarder.core.tests.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import top.kguarder.core.configuration.GuarderConfiguration;
import top.kguarder.core.tests.simple.fallbacker.MockSimpleFallbacker;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {GuarderConfiguration.class, KGuarderSimpleMethodTests.MockServicesConfiguration.class})
public class KGuarderSimpleMethodTests {

    @SpyBean
    private MockSimpleCallService mockSimpleCallService;

    @SpyBean
    private MockSimpleFallbacker mockSimpleFallbacker;

    @Test
    void shouldReturnResult() {
        final Long actual = mockSimpleCallService.returnCall();
        Assertions.assertEquals(200L, actual);
        verify(mockSimpleCallService, times(1)).returnCall();
    }

    @Test
    void shouldReturnResultEx() {
        Assertions.assertThrows(IllegalStateException.class, () -> mockSimpleCallService.returnCallEx());
        verify(mockSimpleCallService, times(1)).returnCallEx();
    }

    @Test
    void shouldReturnResultWhenRetryOnce() {
        final Long actual = mockSimpleCallService.returnSimpleCall();
        Assertions.assertEquals(200L, actual);
        verify(mockSimpleCallService, times(2)).returnSimpleCall();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnFallbackResultWhenRetryFailed() {
        final var actual = (List<String>) mockSimpleCallService.returnSimpleObjectRecoverCall();
        Assertions.assertEquals(3, actual.size());
        verify(mockSimpleCallService, times(4)).returnSimpleObjectRecoverCall();
        verify(mockSimpleFallbacker, only()).fallback(any());
    }

    @Test
    void shouldThrowExceptionWhenCallMethod() {
        Assertions.assertThrows(IllegalStateException.class, () -> mockSimpleCallService.throwExceptionMatchExcludeExCall());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnFallbackResultWhenRetryFailedSinceTimeout() {
        final var actual = (List<String>) mockSimpleCallService.returnSimpleObjectRecoverCallWithTimeout();
        Assertions.assertEquals(3, actual.size());
        verify(mockSimpleCallService, times(1)).returnSimpleObjectRecoverCallWithTimeout();
        verify(mockSimpleFallbacker, only()).fallback(any());
    }

    public static class MockServicesConfiguration {
        @Bean("mockSimpleFallbacker")
        public MockSimpleFallbacker mockSimpleFallbacker() {
            return new MockSimpleFallbacker();
        }
    }
}
