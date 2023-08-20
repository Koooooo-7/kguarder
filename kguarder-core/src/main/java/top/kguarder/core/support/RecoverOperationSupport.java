package top.kguarder.core.support;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.kguarder.core.advisor.GuarderMethodInvoker;
import top.kguarder.core.advisor.GuarderMethodInvokerContext;
import top.kguarder.core.annotation.Recover;
import top.kguarder.core.annotation.Guarder;
import top.kguarder.core.annotation.Retry;
import top.kguarder.core.retry.CustomFailureChecker;
import top.kguarder.core.exception.GuarderThrowableWrapper;
import top.kguarder.core.recover.Fallbacker;
import top.kguarder.core.recover.RecoverContext;
import top.kguarder.core.retry.RetryContext;
import top.kguarder.core.retry.RetryManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class RecoverOperationSupport implements BeanFactoryAware {
    private static final CustomFailureChecker EMPTY_CUSTOM_RESULT_FAILURE_CHECKER = (Result guardedResult) -> false;
    private ThreadPoolTaskExecutor guarderExecutor;
    public GuarderContext guarderContext;

    private BeanFactory beanFactory;

    private RetryManager defaultRetryManager;

    protected Object doInvoke(GuarderMethodInvokerContext guarderMethodInvokerContext) throws Throwable {

        GuardedResult guardedResult =
                doRun(guarderMethodInvokerContext, this.guarderContext.getTimeout(), this.guarderContext.getTimeoutUnit());

        if (guardedResult.isSuccess()) {
            return guardedResult.getFinalResult();
        }

        // do retry?
        if (this.guarderContext.tryRetry()) {
            guardedResult = doRetry(guarderMethodInvokerContext, guardedResult);
        }

        // do recover?
        if (guardedResult.isFailed() && this.guarderContext.tryRecover()) {
            guardedResult = doRecover(guardedResult);
        }
        // final result
        return guardedResult.getFinalResult();

    }

    protected void parserGuarderContext(Method target) {
        final Guarder guarder = target.getAnnotation(Guarder.class);
        final Retry retry = guarder.retry();

        this.guarderContext = new GuarderContext();

        final long timeout = guarder.timeout();
        Assert.isTrue(timeout >= 0, "Timeout should be positive !");

        this.guarderContext.setTimeout(timeout);
        this.guarderContext.setTimeoutUnit(guarder.timeoutUnit());

        if (retry.retryTimes() > 0) {
            CustomFailureChecker customFailureChecker = EMPTY_CUSTOM_RESULT_FAILURE_CHECKER;
            if (StringUtils.isNotEmpty(guarder.failureCustomChecker())) {
                customFailureChecker = getBean(guarder.failureCustomChecker(), CustomFailureChecker.class);
            }

            RetryManager retryManager = defaultRetryManager;

            if (StringUtils.isNotEmpty(guarder.failureCustomChecker())) {
                customFailureChecker = getBean(guarder.failureCustomChecker(), CustomFailureChecker.class);
            }

            if (StringUtils.isNotEmpty(guarder.retry().retryManager())) {
                retryManager = getBean(guarder.retry().retryManager(), RetryManager.class);
            }

            final RetryContext retryContext = RetryContext.builder()
                    .timeout(guarder.timeout())
                    .timeoutUnit(guarder.timeoutUnit())
                    .includeEx(guarder.includeEx())
                    .excludeEx(guarder.excludeEx())
                    .retryTimes(retry.retryTimes())
                    .delay(retry.delay())
                    .customFailureChecker(customFailureChecker)
                    .delayTimeUnit(retry.delayTimeUnit())
                    .delayStrategy(retry.delayStrategy())
                    .retryManager(retryManager)
                    .build();

            this.guarderContext.setRetryContext(retryContext);

        }

        final Recover recover = guarder.recover();
        if (StringUtils.isNotEmpty(recover.fallback())) {
            RecoverContext recoverContext = RecoverContext.builder()
                    .fallback(getBean(recover.fallback(), Fallbacker.class)).build();
            this.guarderContext.setRecoverContext(recoverContext);
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected <T> T getBean(String beanName, Class<T> expectedType) {
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, expectedType, beanName);
    }

    protected GuardedResult doRetry(GuarderMethodInvokerContext guarderMethodInvokerContext, GuardedResult firstCallResult) throws Throwable {
        final RetryContext retryContext = this.guarderContext.getRetryContext();
        final RetryManager retryManager = retryContext.getRetryManager();

        GuardedResult guardedResult = firstCallResult;

        while (retryManager.canRetry(retryContext, guardedResult)) {
            try {
                guardedResult = doRun(guarderMethodInvokerContext, retryContext.getTimeout(), retryContext.getTimeoutUnit());
            } finally {
                retryContext.incRetryTimes();
            }
        }
        return guardedResult;

    }

    protected GuardedResult doRecover(GuardedResult guardedResult) {
        final RecoverContext recoverContext = this.guarderContext.getRecoverContext();
        final Object fallback = recoverContext.getFallback().fallback(new ResultWrapper(guardedResult));

        GuardedResult recoveredResult = new GuardedResult();
        recoveredResult.setSuccess(true);
        recoveredResult.setMethodInvocation(guardedResult.getMethodInvocation());
        recoveredResult.setResult(fallback);
        return recoveredResult;
    }

    protected GuardedResult doRun(GuarderMethodInvokerContext guarderMethodInvokerContext, long timeout, TimeUnit timeUnit) {
        final GuarderMethodInvoker methodInvoker = guarderMethodInvokerContext.getGuarderMethodInvoker();
        GuardedResult guardedResult = new GuardedResult();
        guardedResult.setMethodInvocation(guarderMethodInvokerContext.getOriginalMethodInvoker());
        // straight run
        if (timeout == 0) {
            try {
                final Object result = methodInvoker.invoke();
                guardedResult.setResult(result);
            } catch (GuarderThrowableWrapper e) {
                guardedResult.setThrowableWrapper(e);
            }
            return guardedResult;
        }

        // timeout
        try {
            final Future<Object> future = guarderExecutor.submit(new GuarderRunner(methodInvoker));
            final Object result = future.get(timeout, timeUnit);
            guardedResult.setResult(result);
        } catch (GuarderThrowableWrapper e) {
            guardedResult.setThrowableWrapper(e);
        } catch (Throwable e) {
            guardedResult.setThrowableWrapper(new GuarderThrowableWrapper(e, guarderMethodInvokerContext.getOriginalMethodInvoker()));
        }
        return guardedResult;
    }

    public void setDefaultRetryManager(RetryManager defaultRetryManager) {
        this.defaultRetryManager = defaultRetryManager;
    }

    public void setGuarderExecutor(ThreadPoolTaskExecutor guarderExecutor) {
        this.guarderExecutor = guarderExecutor;
    }
}
