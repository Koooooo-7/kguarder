package top.ko8e24.kguarder.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Recover {

    /*
     * The bean name, which is the instance of {@link Fallbacker}
     * Fallback the method result, support return any result of the method return type sub classes
     * Ideally, The fallback logic should be simple and with error handling is better
     *
     * @return fallback bean name
     */
    String fallback() default "";

}
