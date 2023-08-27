package top.ko8e24.kguarder.core.advisor;

import org.aopalliance.intercept.MethodInvocation;

public class GuarderMethodInvokerContext {
    private final GuarderMethodInvoker guarderMethodInvoker;
    private final GuarderMethodInvoker.MethodInvocationWrapper originalMethodInvoker;

    public GuarderMethodInvokerContext(GuarderMethodInvoker guarderMethodInvoker, MethodInvocation originalMethodInvoker) {
        this.guarderMethodInvoker = guarderMethodInvoker;
        this.originalMethodInvoker = new GuarderMethodInvoker.MethodInvocationWrapper(originalMethodInvoker);
    }

    public GuarderMethodInvoker getGuarderMethodInvoker() {
        return guarderMethodInvoker;
    }

    public MethodInvocation getOriginalMethodInvoker() {
        return originalMethodInvoker;
    }

    @FunctionalInterface
    public interface GuarderMethodInvokerSetter {
        OriginalMethodInvokerSetter guarderInvoker(GuarderMethodInvoker invoker);
    }

    @FunctionalInterface
    public interface OriginalMethodInvokerSetter {
        GuarderMethodInvokerContext methodInvoker(MethodInvocation invoker);
    }

    public static GuarderMethodInvokerSetter newCtx() {
        return guarderMethodInvoker -> methodInvocation -> new GuarderMethodInvokerContext(guarderMethodInvoker, methodInvocation);
    }
}
