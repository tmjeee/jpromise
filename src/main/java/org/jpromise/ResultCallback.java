package org.jpromise;

@FunctionalInterface
public interface ResultCallback<I> {
    void call(I i);
}
