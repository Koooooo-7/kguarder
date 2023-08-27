package top.ko8e24.kguarder.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    // how much times you want to do retry after method failed
    int retryTimes() default 0;

    // how long the delay time is when you want to do retry when method failed
    long delay() default 1;

    // the delay time unit is when you want to do retry when method failed
    TimeUnit delayTimeUnit() default TimeUnit.SECONDS;

    DelayStrategy delayStrategy() default DelayStrategy.FIXED;

    String retryManager() default "";

    enum DelayStrategy {

        /*
         * FIXED delay time to run
         * MULTIPLIER will be longer to run the task
         * RANDOM time to run
         * <p>
         * It has some magic coefficient to make the delay time more reasonable
         */
        FIXED(((delay, delayTimeUnit, totalRetryTimes, currentRetryTimes) -> {
            return delayTimeUnit.toMillis(delay);
        })),

        MULTIPLIER((delay, delayTimeUnit, totalRetryTimes, currentRetryTimes) -> {
            return delayTimeUnit.toMillis(delay * currentRetryTimes);
        }),

        RANDOM((delay, delayTimeUnit, totalRetryTimes, currentRetryTimes) -> {

            final double wave = Math.min(0.8, currentRetryTimes * 0.2);
            final long fixed = delayTimeUnit.toMillis(delay);
            final long low = (long) (fixed * (1 - wave));
            final long high = (long) (fixed * (1 + wave));
            return ThreadLocalRandom.current().nextLong(low, high);
        });

        private final RetryDelayCalculator calculator;

        DelayStrategy(RetryDelayCalculator calculator) {
            this.calculator = calculator;
        }

        public RetryDelayCalculator getDelayCalculator() {
            return calculator;
        }
    }


    @FunctionalInterface
    interface RetryDelayCalculator {

        // calculate next run time to mills
        long calculate(long delay, TimeUnit delayTimeUnit, int totalRetryTimes, int currentRetryTimes);

    }
}
