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
        final long rdm2 = random.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 1);
        final long rdm3 = random.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 1);
        Assertions.assertNotEquals(rdm1, rdm2);
        Assertions.assertNotEquals(rdm2, rdm3);
        Assertions.assertNotEquals(rdm1, rdm3);
    }

    @Test
    void shouldReturnMultipleTimeWhenCallMultipleStrategy() {
        final Retry.DelayStrategy multiplier = Retry.DelayStrategy.MULTIPLIER;
        final long m1 = multiplier.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 1);
        final long m2 = multiplier.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 2);
        final long m3 = multiplier.getDelayCalculator().calculate(1L, TimeUnit.SECONDS, 3, 3);
        Assertions.assertEquals(m1 * 2, m2);
        Assertions.assertEquals(m1 * 3, m3);
    }
}
