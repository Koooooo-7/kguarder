package top.kguarder.core.advisor;

import top.kguarder.core.annotation.Guarder;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.Objects;

public class GuarderPointcut extends StaticMethodMatcherPointcut {
    @Override
    public boolean matches(Method method, @NonNull Class<?> targetClass) {
        final Guarder guarder = method.getAnnotation(Guarder.class);
        return Objects.nonNull(guarder);
    }


}
