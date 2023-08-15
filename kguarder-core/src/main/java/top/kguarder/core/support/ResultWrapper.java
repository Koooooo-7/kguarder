package top.kguarder.core.support;

import top.kguarder.core.exception.GuarderThrowableWrapper;
import top.kguarder.core.exception.GuarderException;
import lombok.Data;

import java.util.Optional;

@Data
public class ResultWrapper {

    private boolean success;

    private Object result;

    private GuarderThrowableWrapper throwableWrapper;

    public ResultWrapper() {
    }

    public ResultWrapper(Object result) {
    }

    public <T> Optional<T> getResult() {
        return Optional.ofNullable((T) result);
    }

    protected Object getFinalResult() {
        if (isFailed()) {
            throw new GuarderException("Guarder handle method finished, it is still failed, result is [" + getResult().orElse("") + "]",
                    throwableWrapper.getOriginal());
        }
        return result;
    }

    public GuarderThrowableWrapper getThrowableWrapper() {
        return throwableWrapper;
    }

    public boolean isFailed() {
        return !success;
    }
}
