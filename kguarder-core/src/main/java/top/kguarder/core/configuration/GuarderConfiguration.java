package top.kguarder.core.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.kguarder.core.advisor.GuarderAdvisor;
import top.kguarder.core.advisor.GuarderInterceptor;
import top.kguarder.core.retry.DefaultRetryManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import top.kguarder.core.retry.RetryManager;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GuarderProperties.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class GuarderConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public GuarderAdvisor guarderAdvisor(GuarderProperties guarderProperties, GuarderInterceptor guarderInterceptor) {
        GuarderAdvisor advisor = new GuarderAdvisor();
        advisor.setAdvice(guarderInterceptor);
        advisor.setOrder(Ordered.LOWEST_PRECEDENCE + guarderProperties.getAdviceOrderAdjust());
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public GuarderInterceptor guarderInterceptor(@Qualifier("guarderExecutor") ThreadPoolTaskExecutor guarderExecutor,
                                                 RetryManager retryManager) {
        final GuarderInterceptor guarderInterceptor = new GuarderInterceptor();
        guarderInterceptor.setDefaultRetryManager(retryManager);
        guarderInterceptor.setGuarderExecutor(guarderExecutor);
        return guarderInterceptor;
    }

    @Bean
    @Primary
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RetryManager retryManager() {
        return new DefaultRetryManager();
    }


    @Bean("guarderExecutor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ThreadPoolTaskExecutor guarderExecutor(GuarderProperties guarderProperties) {
        final ExecutorProperties properties = guarderProperties.getExecutor();

        final Duration keepAliveDuration = properties.getKeepAliveDuration();
        final long seconds = keepAliveDuration.getSeconds();

        ThreadPoolTaskExecutor guarderExecutor = new ThreadPoolTaskExecutor();
        guarderExecutor.setCorePoolSize(properties.getCorePoolSize());
        guarderExecutor.setMaxPoolSize(properties.getMaximumPoolSize());
        guarderExecutor.setKeepAliveSeconds((int) seconds);
        guarderExecutor.setAllowCoreThreadTimeOut(true);
        guarderExecutor.setThreadNamePrefix("guarder-worker");
        guarderExecutor.setQueueCapacity(properties.getWorkQueueSize());
        guarderExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        guarderExecutor.setDaemon(true);
        guarderExecutor.initialize();
        return guarderExecutor;
    }


}
