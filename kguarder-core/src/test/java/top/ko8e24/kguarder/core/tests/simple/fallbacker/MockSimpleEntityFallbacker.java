package top.ko8e24.kguarder.core.tests.simple.fallbacker;

import top.ko8e24.kguarder.core.recover.Fallbacker;
import top.ko8e24.kguarder.core.support.Result;

public class MockSimpleEntityFallbacker implements Fallbacker {

    @Override
    public MyFoo fallback(Result guardedResult) {
        return guardedResult.<MyFoo>resultHelper(
                (result, target) ->
                        result.getCode() == 200L ? result : new MyFoo(200L, "correct fallback result"),
                (throwable, target) -> new MyFoo(500L, "handle with 500"));
    }

}
