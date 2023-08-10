package top.kguarder.core.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("kguarder")
@Data
public class GuarderProperties {

    /**
     * Normally this should be after by @Transaction and @Cacheable
     * By default is {@Ordered.LOWEST_PRECEDENCE -1}
     * the order +/- based on {@Ordered.LOWEST_PRECEDENCE}
     */
    private int adviceOrderAdjust = -1;

    @NestedConfigurationProperty
    private ExecutorProperties executor = new ExecutorProperties();


}
