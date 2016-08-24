package org.jpromise;

@FunctionalInterface
public interface TaskCallback {
    void call(Resolution resolution);
}
