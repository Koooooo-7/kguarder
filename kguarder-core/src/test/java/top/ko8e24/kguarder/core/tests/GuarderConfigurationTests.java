package top.ko8e24.kguarder.core.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.ko8e24.kguarder.core.advisor.GuarderAdvisor;
import top.ko8e24.kguarder.core.advisor.GuarderInterceptor;
import top.ko8e24.kguarder.core.configuration.ExecutorProperties;
import top.ko8e24.kguarder.core.configuration.GuarderConfiguration;
import top.ko8e24.kguarder.core.configuration.GuarderProperties;
import top.ko8e24.kguarder.core.retry.DefaultRetryManager;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuarderConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GuarderConfiguration.class));

    @Test
    void checkRequiredDefaultBeansCreated() {
        this.contextRunner.run((context) -> {
            // properties
            assertThat(context).getBean(GuarderProperties.class)
                    .hasFieldOrProperty("adviceOrderAdjust")
                    .extracting("adviceOrderAdjust")
                    .isEqualTo(-1);
            assertThat(context).getBean(GuarderProperties.class)
                    .hasFieldOrProperty("executor")
                    .extracting("executor")
                    .isInstanceOfSatisfying(ExecutorProperties.class, executorProperties -> {
                        Assertions.assertEquals(5, executorProperties.getCorePoolSize());
                        Assertions.assertEquals(10, executorProperties.getMaximumPoolSize());
                        Assertions.assertEquals(Duration.ofMinutes(20).getSeconds(), executorProperties.getKeepAliveDuration().getSeconds());
                        Assertions.assertEquals(2000, executorProperties.getWorkQueueSize());
                    });

            // beans
            assertThat(context).hasSingleBean(DefaultRetryManager.class);
            assertThat(context).getBean("retryManager").isSameAs(context.getBean(DefaultRetryManager.class));

            assertThat(context).hasSingleBean(GuarderInterceptor.class);
            assertThat(context).getBean("guarderInterceptor").isSameAs(context.getBean(GuarderInterceptor.class));

            assertThat(context).hasSingleBean(GuarderAdvisor.class);
            assertThat(context).getBean("guarderAdvisor").isSameAs(context.getBean(GuarderAdvisor.class));

            assertThat(context).hasSingleBean(ThreadPoolTaskExecutor.class);
            assertThat(context).getBean("guarderExecutor").isSameAs(context.getBean(ThreadPoolTaskExecutor.class));

            assertThat(context).hasSingleBean(DefaultAdvisorAutoProxyCreator.class);
            assertThat(context).getBean("defaultAdvisorAutoProxyCreator").isSameAs(context.getBean(DefaultAdvisorAutoProxyCreator.class));

        });
    }


    @Test
    void checkConfigurationCreated() {
        this.contextRunner.withPropertyValues(
                "guarder.advice-order-adjust=3",
                "guarder.executor.core-pool-size=3",
                "guarder.executor.keep-alive-duration=100m",
                "guarder.executor.work-queue-size=3000"
        ).run((context) -> {
            // properties
            assertThat(context).getBean(GuarderProperties.class)
                    .hasFieldOrProperty("adviceOrderAdjust")
                    .extracting("adviceOrderAdjust")
                    .isEqualTo(3);
            assertThat(context).getBean(GuarderProperties.class)
                    .hasFieldOrProperty("executor")
                    .extracting("executor")
                    .isInstanceOfSatisfying(ExecutorProperties.class, executorProperties -> {
                        Assertions.assertEquals(3, executorProperties.getCorePoolSize());
                        Assertions.assertEquals(10, executorProperties.getMaximumPoolSize());
                        Assertions.assertEquals(Duration.ofMinutes(100).getSeconds(), executorProperties.getKeepAliveDuration().getSeconds());
                        Assertions.assertEquals(3000, executorProperties.getWorkQueueSize());
                    });

        });
    }


}
