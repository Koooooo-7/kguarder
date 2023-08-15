package top.kguarder.core.advisor;

import top.kguarder.core.support.RecoverOperationSupport;
import top.kguarder.core.exception.GuarderThrowableWrapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.NonNull;

public class GuarderInterceptor extends RecoverOperationSupport implements MethodInterceptor {
    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {

        parserGuarderContext(invocation.getMethod());

        GuarderMethodInvoker guarderMethodInvoker = () -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw new GuarderThrowableWrapper(e, invocation);
            }
        };

        return doInvoke(GuarderMethodInvokerContext.newCtx()
                .guarderInvoker(guarderMethodInvoker)
                .methodInvoker(new GuarderMethodInvoker.MethodInvocationWrapper(invocation)));
    }
}
