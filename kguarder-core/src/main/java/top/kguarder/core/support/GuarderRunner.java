package top.kguarder.core.support;

import top.kguarder.core.advisor.GuarderMethodInvoker;
import top.kguarder.core.exception.GuarderThrowableWrapper;
import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;

@AllArgsConstructor
public class GuarderRunner implements Callable<Object> {
    public GuarderMethodInvoker invoker;

    @Override
    public Object call() throws GuarderThrowableWrapper {
        return invoker.invoke();
    }
}
