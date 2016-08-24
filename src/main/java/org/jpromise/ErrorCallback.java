package org.jpromise;

@FunctionalInterface
public interface ErrorCallback<E extends Throwable> {
    void call(E e);
}
