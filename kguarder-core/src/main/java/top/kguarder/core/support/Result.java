package top.kguarder.core.support;

import org.aopalliance.intercept.MethodInvocation;

import java.util.Optional;

public interface Result {

    <T> Optional<T> get();

    MethodInvocation getTarget();

    Optional<Throwable> getThrowable();
}
