package top.kguarder.core.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.kguarder.core.advisor.GuarderMethodInvoker;
import top.kguarder.core.annotation.Recover;
import top.kguarder.core.annotation.Guarder;
import top.kguarder.core.annotation.Retry;
import top.kguarder.core.component.FailureCustomChecker;
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
    private static final FailureCustomChecker EMPTY_CUSTOM_RESULT_FAILURE_CHECKER = (ResultWrapper resultWrapper) -> false;
    private ThreadPoolTaskExecutor guarderExecutor;
    public GuarderContext guarderContext;

    private BeanFactory beanFactory;

    private RetryManager defaultRetryManager;

    public Object doInvoke(GuarderMethodInvoker guarderMethodInvoker) {


        ResultWrapper resultWrapper =
                doRun(guarderMethodInvoker, this.guarderContext.getTimeout(), this.guarderContext.getTimeoutUnit());
        // do retry?
        if (this.guarderContext.tryRetry()) {
            resultWrapper = doRetry(guarderMethodInvoker, resultWrapper);
        }

        // do recover?
        if (resultWrapper.isFailed() && this.guarderContext.tryRecover()) {
            resultWrapper = doRecover(resultWrapper);
        }
        // final result
        return resultWrapper.getFinalResult();

    }

    public void parserGuarderContext(Method target) {
        final Guarder guarder = target.getAnnotation(Guarder.class);
        final Retry retry = guarder.retry();

        this.guarderContext = new GuarderContext();

        final long timeout = guarder.timeout();
        Assert.isTrue(timeout >= 0, "Timeout should be positive !");

        this.guarderContext.setTimeout(timeout);
        this.guarderContext.setTimeoutUnit(guarder.timeoutUnit());

        if (retry.retryTimes() > 0) {
            FailureCustomChecker failureCustomChecker = EMPTY_CUSTOM_RESULT_FAILURE_CHECKER;
            if (StringUtils.isNotEmpty(guarder.failureCheckCustomizer())) {
                failureCustomChecker = getBean(guarder.failureCheckCustomizer(), FailureCustomChecker.class);
            }

            RetryManager retryManager = defaultRetryManager;

            if (StringUtils.isNotEmpty(guarder.failureCheckCustomizer())) {
                failureCustomChecker = getBean(guarder.failureCheckCustomizer(), FailureCustomChecker.class);
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
                    .failureCustomChecker(failureCustomChecker)
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

    public ResultWrapper doRetry(GuarderMethodInvoker invoker, ResultWrapper firstCallResult) {
        final RetryContext retryContext = this.guarderContext.getRetryContext();
        final RetryManager retryManager = retryContext.getRetryManager();

        ResultWrapper resultWrapper = firstCallResult;

        while (retryManager.canRetry(retryContext, resultWrapper)) {
            try {
                resultWrapper = doRun(invoker, retryContext.getTimeout(), retryContext.getTimeoutUnit());
            } finally {
                retryContext.incRetryTimes();
            }
        }
        return resultWrapper;

    }

    public ResultWrapper doRecover(ResultWrapper resultWrapper) {
        final RecoverContext recoverContext = this.guarderContext.getRecoverContext();
        final Object fallback = recoverContext.getFallback().fallback(resultWrapper);

        ResultWrapper recoveredResult = new ResultWrapper();
        recoveredResult.setSuccess(true);
        recoveredResult.setResult(fallback);
        return recoveredResult;
    }

    public ResultWrapper doRun(GuarderMethodInvoker methodInvoker, long timeout, TimeUnit timeUnit) {
        ResultWrapper resultWrapper = new ResultWrapper();
        // straight run
        if (timeout == 0) {
            try {
                final Object result = methodInvoker.invoke();
                resultWrapper.setResult(result);
            } catch (GuarderThrowableWrapper e) {
                // error
                resultWrapper.setThrowableWrapper(e);
            }
            return resultWrapper;
        }

        // timeout
        try {
            final Future<Object> future = guarderExecutor.submit(new GuarderRunner(methodInvoker));
            final Object result = future.get(timeout, timeUnit);
            resultWrapper.setResult(result);
        } catch (GuarderThrowableWrapper e) {
            resultWrapper.setThrowableWrapper(e);
        } catch (Exception e) {
            resultWrapper.setThrowableWrapper(new GuarderThrowableWrapper(e, null));
        }
        return resultWrapper;
    }

    public void setDefaultRetryManager(RetryManager defaultRetryManager) {
        this.defaultRetryManager = defaultRetryManager;
    }

    public void setGuarderExecutor(ThreadPoolTaskExecutor guarderExecutor) {
        this.guarderExecutor = guarderExecutor;
    }
}
