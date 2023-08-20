package top.kguarder.core.support;


@FunctionalInterface
public interface Helper<RESULT, TARGET, R> {
    R handle(RESULT r, TARGET t);
}
