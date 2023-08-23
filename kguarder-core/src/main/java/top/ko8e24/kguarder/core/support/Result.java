package top.ko8e24.kguarder.core.support;

import org.aopalliance.intercept.MethodInvocation;
import top.ko8e24.kguarder.core.exception.GuarderException;

import java.util.Optional;

public interface Result {

    // the method call result, if no ex
    <T> Optional<T> get();

    // call method target meta
    MethodInvocation getTarget();

    // ex in this method call
    Optional<Throwable> getThrowable();


    @SuppressWarnings("unchecked")
    default <T> T resultHelper(Helper<T, MethodInvocation, ? extends T> resultHelper, Helper<Throwable, MethodInvocation, ? extends T> throwableHelper) {
        var result = get().map(re -> resultHelper.handle((T) re, getTarget()));
        if (result.isPresent()) {
            return result.get();
        }

        return getThrowable()
                .map(throwable -> throwableHelper.handle(throwable, getTarget()))
                .orElseThrow(() -> new GuarderException("Can not handle this", getThrowable().get()));
    }

    default <T> T resultHelperHandleResultOnly(Helper<T, MethodInvocation, ? extends T> resultHelper) {
        return resultHelper(resultHelper, (throwable, target) -> {
            throw new GuarderException("Can not handle this", getThrowable().get());
        });
    }

    default <T> T resultHelperHandleThrowableOnly(Helper<Throwable, MethodInvocation, ? extends T> throwableHelp) {
        return resultHelper((result, target) -> result, throwableHelp);
    }
}
