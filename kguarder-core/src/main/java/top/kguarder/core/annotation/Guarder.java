package top.kguarder.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * The main Guarder annotation, the guarder which contains two options either or both
 * - @Retry, provide retry options
 * - @Recover, provide the recover options
 * Usage:
 * Guarder(retry = @Retry(retryTimes = 5, delay = 2, delayStrategy = Retry.DelayStrategy.MULTIPLIER),
 * failureCheckCustomizer = "MyChecker",
 * recover = @Recover(fallback = "myFallback"))
 *
 * @author Koy Zhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Guarder {

    /*
     * Whether marked method has timeout failed requirement, e.g. the method should return in `3 seconds`.
     * Default is 0 means no timeout required, this MUST be positive
     */
    long timeout() default 0L;

    TimeUnit timeoutUnit() default TimeUnit.SECONDS;

    /**
     * Allow to customize what is failed result on current method call, which means it allows to
     * identify the method result on business failed, i.e.
     * A return result with failed with bizResultCode=50000 in payload after calling other service
     *
     * @return the bean name which is the instance of {@link  top.kguarder.core.component.CustomFailureChecker }
     */

    String failureCustomChecker() default "";

    // exception exclude, will throw directly
    Class<? extends Exception>[] excludeEx() default {};

    // exception include, will swallow it into retry/recover
    Class<? extends Exception>[] includeEx() default Exception.class;

    // retry config
    Retry retry() default @Retry();

    // recover config
    Recover recover() default @Recover();

}
