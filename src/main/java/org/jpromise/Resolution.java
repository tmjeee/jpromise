package org.jpromise;

public class Resolution {

    private final Promise.LockingMechanism lockingMechanism;
    private Object result;
    private Throwable error;
    private boolean isRejected = false;
    private boolean hasResult = false;

    Resolution(Promise.LockingMechanism lockingMechanism) {
        this.lockingMechanism = lockingMechanism;
    }


    public <R> void resolve(R r) {
        lockingMechanism.lockAndNotify(()->{
            result = r;
            hasResult = true;
        });
    }

    <R, E extends Throwable> void reject(R r, E t) {
        lockingMechanism.lockAndNotify(()->{
            error = t;
            result = r;
            isRejected = true;
        });
    }

    public <E extends Throwable> void reject(E t) {
        lockingMechanism.lockAndNotify(() -> {
            error = t;
            isRejected = true;
        });
    }

    boolean isRejected() {
        return isRejected;
    }

    Throwable getException() {
        return error;
    }

    <R> R getResult() {
        return (R) result;
    }

    boolean hasResult() {
        return hasResult;

    }
}
