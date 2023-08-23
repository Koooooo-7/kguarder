package top.ko8e24.kguarder.core.advisor;

import top.ko8e24.kguarder.core.exception.GuarderThrowableWrapper;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

@FunctionalInterface
public interface GuarderMethodInvoker {

    @Nullable
    Object invoke() throws GuarderThrowableWrapper;

    class MethodInvocationWrapper implements MethodInvocation {
        private final MethodInvocation delegate;

        public MethodInvocationWrapper(MethodInvocation delegate) {
            this.delegate = delegate;
        }

        @Override
        public Method getMethod() {
            return delegate.getMethod();
        }

        @Override
        public Object[] getArguments() {
            return delegate.getArguments();
        }

        @Override
        public Object proceed() throws Throwable {
            // do nothing
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getThis() {
            return delegate.getThis();
        }

        @Override
        public AccessibleObject getStaticPart() {
            return delegate.getStaticPart();
        }
    }
}
