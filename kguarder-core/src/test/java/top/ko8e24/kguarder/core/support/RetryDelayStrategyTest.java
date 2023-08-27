package top.ko8e24.kguarder.core.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.ko8e24.kguarder.core.annotation.Retry;

import java.util.concurrent.TimeUnit;

public class RetryDelayStrategyTest {

    @Test
    void shouldReturnRandomTimeWhenCallRandomStrategy() {
        final Retry.DelayStrategy random = Retry.DelayStrategy.RANDOM;
        final long rdm1 = random.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 1);
        final long rdm2 = random.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 2);
        final long rdm3 = random.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 3);
        Assertions.assertNotEquals(rdm1, rdm2);
        Assertions.assertNotEquals(rdm2, rdm3);
        Assertions.assertNotEquals(rdm1, rdm3);
    }
}
