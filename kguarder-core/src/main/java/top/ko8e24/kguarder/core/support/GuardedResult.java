package top.ko8e24.kguarder.core.support;

import org.aopalliance.intercept.MethodInvocation;
import top.ko8e24.kguarder.core.advisor.GuarderMethodInvoker;
import top.ko8e24.kguarder.core.exception.GuarderThrowableWrapper;
import top.ko8e24.kguarder.core.exception.GuarderException;
import lombok.Data;

import java.util.Optional;

@Data
public class GuardedResult implements Result {

    private boolean success;

    private Object result;

    private GuarderMethodInvoker.MethodInvocationWrapper methodInvocation;

    private GuarderThrowableWrapper throwableWrapper;

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get() {
        return Optional.ofNullable((T) result);
    }

    @Override
    public MethodInvocation getTarget() {
        return methodInvocation;
    }

    @Override
    public Optional<Throwable> getThrowable() {
        return Optional.ofNullable(throwableWrapper).isPresent() ? Optional.ofNullable(throwableWrapper.getOriginal()) : Optional.empty();
    }

    protected Object getFinalResult() {
        if (isFailed()) {
            throw new GuarderException("Guarder handle method finished, it is still failed, result is [" + get().orElse("") + "]",
                    throwableWrapper.getOriginal());
        }
        return result;
    }

    public GuarderThrowableWrapper getThrowableWrapper() {
        return throwableWrapper;
    }

    public boolean isFailed() {
        return !success;
    }
}
