package top.kguarder.core.support;

import top.kguarder.core.retry.RetryContext;
import top.kguarder.core.recover.RecoverContext;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
public class GuarderContext {


    private long timeout;

    private TimeUnit timeoutUnit;

    private ResultWrapper result;

    private RetryContext retryContext;

    private RecoverContext recoverContext;

    public boolean tryRetry() {
        return Objects.nonNull(retryContext);
    }

    public boolean tryRecover() {
        return Objects.nonNull(recoverContext);
    }


}
