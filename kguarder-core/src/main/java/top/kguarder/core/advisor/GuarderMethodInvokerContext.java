package top.kguarder.core.advisor;

public class GuarderMethodInvokerContext {
    private final GuarderMethodInvoker guarderMethodInvoker;
    private final GuarderMethodInvoker.MethodInvocationWrapper originalMethodInvoker;

    public GuarderMethodInvokerContext(GuarderMethodInvoker guarderMethodInvoker, GuarderMethodInvoker.MethodInvocationWrapper originalMethodInvoker) {
        this.guarderMethodInvoker = guarderMethodInvoker;
        this.originalMethodInvoker = originalMethodInvoker;
    }

    public GuarderMethodInvoker getGuarderMethodInvoker() {
        return guarderMethodInvoker;
    }

    public GuarderMethodInvoker.MethodInvocationWrapper getOriginalMethodInvoker() {
        return originalMethodInvoker;
    }

    @FunctionalInterface
    public interface GuarderMethodInvokerSetter {
        OriginalMethodInvokerSetter guarderInvoker(top.kguarder.core.advisor.GuarderMethodInvoker invoker);
    }

    @FunctionalInterface
    public interface OriginalMethodInvokerSetter {
        GuarderMethodInvokerContext methodInvoker(GuarderMethodInvoker.MethodInvocationWrapper invoker);
    }

    public static GuarderMethodInvokerSetter newCtx() {
        return guarderMethodInvoker -> methodInvocationWrapper -> new GuarderMethodInvokerContext(guarderMethodInvoker, methodInvocationWrapper);
    }
}
