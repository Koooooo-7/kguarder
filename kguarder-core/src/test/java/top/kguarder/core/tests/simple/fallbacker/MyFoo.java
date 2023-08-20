package top.kguarder.core.tests.simple.fallbacker;

public class MyFoo {
    private final Long code;
    private final String bar;

    public MyFoo(Long code, String bar) {
        this.code = code;
        this.bar = bar;
    }

    public Long getCode() {
        return code;
    }

    public String getBar() {
        return bar;
    }
}
