package top.ko8e24.kguarder.core.support;

import top.ko8e24.kguarder.core.advisor.GuarderMethodInvoker;
import top.ko8e24.kguarder.core.exception.GuarderThrowableWrapper;
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
