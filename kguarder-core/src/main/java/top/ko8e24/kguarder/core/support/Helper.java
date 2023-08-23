package top.ko8e24.kguarder.core.support;


@FunctionalInterface
public interface Helper<RESULT, TARGET, R> {
    R handle(RESULT r, TARGET t);
}
