package org.jpromise;

public class Deferred {

    private TaskCallback c;

    public Deferred(TaskCallback c) {
        this.c = c;
    }

    public <I,O,E extends Throwable> Deferred then(TransformingCallback<I,O> t, ErrorCallback<E> e) {
        return null;
    }

    public <I,O> Deferred thenTransform(TransformingCallback<I,O> t) {
        return null;
    }

    public <E extends Throwable> Deferred thenCatch(ErrorCallback<E> e) {
        return null;
    }

    public <R, E extends Throwable> Deferred done(ResultCallback<R> r, ErrorCallback<E> e) {
        return null;
    }

    public <R> Deferred doneResolve(ResultCallback<R> r) {
        return null;
    }

    public <E extends Throwable> Deferred doneReject(ErrorCallback<E> e) {
        return null;
    }


    public static class DeferredFinal {
        public void execute() {
        }
    }
}
