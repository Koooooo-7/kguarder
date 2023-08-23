package top.ko8e24.kguarder.core.exception;

import top.ko8e24.kguarder.core.advisor.GuarderMethodInvoker;
import org.aopalliance.intercept.MethodInvocation;

public class GuarderThrowableWrapper extends RuntimeException {

    private MethodInvocation invocation;
    private final Throwable original;

    public GuarderThrowableWrapper(Throwable original, MethodInvocation invocation) {
        super(original.getMessage(), original);
        this.original = original;
        this.invocation = new GuarderMethodInvoker.MethodInvocationWrapper(invocation);
    }

    public Throwable getOriginal() {
        return this.original;
    }

    public MethodInvocation getInvocation() {
        return invocation;
    }
}
