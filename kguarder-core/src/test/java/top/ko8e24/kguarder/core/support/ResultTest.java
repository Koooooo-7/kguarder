package top.ko8e24.kguarder.core.support;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultHandler;
import top.ko8e24.kguarder.core.exception.GuarderException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    private MockHelper target;

    @BeforeEach
    public void setUp() {
        target = new MockHelper();
    }

    @Test
    void resultHelperWithResult() {

        target.setResult("1");
        final String actual = target.resultHelper((o, mi) -> o + "23", (t, mi) -> "1");
        Assertions.assertEquals("123", actual);
    }

    @Test
    void resultHelperWithThrowable() {
        target.setThrowable(new Exception());
        final String actual = target.resultHelper((o, mi) -> o + "23", (t, mi) -> "1");
        Assertions.assertEquals("1", actual);
    }

    @Test
    void resultHelperHandleResultOnly() {
        target.setResult("1");
        final String actual = target.resultHelperHandleResultOnly((o, mi) -> o + "23");
        Assertions.assertEquals("123", actual);
    }

    @Test
    void resultHelperHandleResultOnlyWithEx() {
        target.setThrowable(new IllegalStateException());
        Assertions.assertThrows(GuarderException.class, () -> target.resultHelperHandleResultOnly((o, mi) -> o + "23"));
    }

    @Test
    void resultHelperHandleThrowableOnly() {
        target.setThrowable(new Exception());
        final String actual = target.resultHelperHandleThrowableOnly((t, mi) -> "1");
        Assertions.assertEquals("1", actual);
    }


    public static class MockHelper implements Result {

        private Object result;
        private Throwable throwable;

        public void setResult(Object result) {
            this.result = result;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public <T> Optional<T> get() {
            return (Optional<T>) Optional.ofNullable(result);
        }

        @Override
        public MethodInvocation getTarget() {
            return null;
        }

        @Override
        public Optional<Throwable> getThrowable() {
            return Optional.ofNullable(throwable);
        }
    }
}