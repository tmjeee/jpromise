package org.jpromise;

@FunctionalInterface
public interface TransformingCallback<I,O> {
    O call(I i);
}
