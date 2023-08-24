package top.ko8e24.kguarder.core.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("guarder")
@Data
public class GuarderProperties {

    /**
     * Normally this should be exec before some annotations behavior such as @Transaction, @Cacheable.
     * By default the Order is {@value org.springframework.core.Ordered#LOWEST_PRECEDENCE -1}
     * adviceOrderAdjust, the adviceOrderAdjust - based on {@value org.springframework.core.Ordered#LOWEST_PRECEDENCE}
     */
    private int adviceOrderAdjust = -1;

    @NestedConfigurationProperty
    private ExecutorProperties executor = new ExecutorProperties();


}
