package top.kguarder.core.exception;

import top.kguarder.core.advisor.GuarderMethodInvoker;
import org.aopalliance.intercept.MethodInvocation;

public class GuarderThrowableWrapper extends RuntimeException {

    private MethodInvocation invocation;
    private final Throwable original;

    public GuarderThrowableWrapper(Throwable original, MethodInvocation invocation) {
        super(original.getMessage(), original);
        this.original = original;
    }

    public Throwable getOriginal() {
        return this.original;
    }

    public MethodInvocation getInvocation() {
        return new GuarderMethodInvoker.MethodInvocationWrapper(invocation);
    }
}
