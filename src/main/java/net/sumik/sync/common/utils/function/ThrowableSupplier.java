package net.sumik.sync.common.utils.function;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    T get() throws Throwable;
}