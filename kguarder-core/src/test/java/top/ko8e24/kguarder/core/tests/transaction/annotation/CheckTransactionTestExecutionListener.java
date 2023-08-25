package top.ko8e24.kguarder.core.tests.transaction.annotation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CheckTransactionTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    public void beforeTestExecution(TestContext testContext) throws Exception {
        invoke(testContext, CheckBeforeTransaction.class, (method -> method.getAnnotation(CheckBeforeTransaction.class).value()));
    }

    @Override
    public void afterTestExecution(TestContext testContext) throws Exception {
        invoke(testContext, CheckAfterTransaction.class, (method -> method.getAnnotation(CheckAfterTransaction.class).value()));
    }

    private void invoke(TestContext testContext, Class<? extends Annotation> annotationType, Function<Method, Tag> resolveTag) throws Exception {
        final var testMethod = testContext.getTestMethod();
        final var tag = testMethod.getAnnotation(Tag.class);
        if (Objects.isNull(tag)) {
            return;
        }

        final var sourceTagVal = tag.value();

        List<Method> methods = getAnnotatedMethods(testContext.getTestClass(), annotationType);
        Collections.reverse(methods);
        for (Method method : methods) {
            final var tagVal = resolveTag.apply(method).value();
            if (StringUtils.equals(sourceTagVal, tagVal)) {
                ReflectionUtils.makeAccessible(method);
                method.invoke(testContext.getTestInstance(), testContext.getTestMethod());
            }
        }
    }


    private List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
        ReflectionUtils.MethodFilter methodFilter = ReflectionUtils.USER_DECLARED_METHODS
                .and(method -> AnnotatedElementUtils.hasAnnotation(method, annotationType));
        return Arrays.asList(ReflectionUtils.getUniqueDeclaredMethods(clazz, methodFilter));
    }


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
