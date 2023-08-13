package top.kguarder.core.annotation;

import top.kguarder.core.configuration.GuarderConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Expose a way to enable @EnableGuarder bootstrap
 *
 * @author Koy Zhuang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({GuarderConfiguration.class})
public @interface EnableGuarder {
}
