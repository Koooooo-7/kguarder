package top.kguarder.core.support;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Optional;

@RequiredArgsConstructor
public class ResultWrapper implements Result {

    private final GuardedResult delegate;

    @Override
    public <T> Optional<T> get() {
        return delegate.get();
    }

    @Override
    public MethodInvocation getTarget() {
        return delegate.getTarget();
    }

    @Override
    public Optional<Throwable> getThrowable() {
        return delegate.getThrowable();
    }
}
