package top.ko8e24.kguarder.core.tests.transaction.annotation;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckBeforeTransaction {
    Tag value();
}
