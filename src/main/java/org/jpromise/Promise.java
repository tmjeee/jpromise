package org.jpromise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static java.lang.String.format;

public class Promise {

    private static final Logger logger = LoggerFactory.getLogger(Promise.class);

    static class InvokingThread extends Thread {
        InvokingThread(Runnable r) {
            super(r);
            setName("InvokingThread");
            setDaemon(true);
        }
    }

    class LockingMechanism {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition resultReady = lock.newCondition();
        private volatile boolean done = false;

        void lockAndWait(Runnable r) throws InterruptedException {
            trace(()->format("lockAndWait isDone %s", done));
            if (!done) {
                lock.lock();
                trace(()->"lockAndWait got lock");
                try {
                    trace(()->"lockAndWait waiting on result");
                    trace(()->"lockAndWait starting invokingThread");
                    new InvokingThread(r).start();
                    resultReady.await();
                } finally {
                    lock.unlock();
                    trace(()->"lockAndWait release lock");
                }
            }
        }

        void lockAndNotify(Runnable r) {
            trace(()->format("lockAndNotify isDone=%s",done));
            if (!done) {
                lock.lock();
                trace(()->"lockAndNotify got lock");
                try {
                    trace(()->"lockAndNotify run runnable");
                    r.run();
                    resultReady.signalAll();
                    trace(()->"lockAndNotify signalAll");
                } finally {
                    done = true;
                    lock.unlock();
                    trace(()->"lockAndNotify release lock");
                }
            }
        }
    }

    public static <R> Promise resolve(R o) {
        return new Promise(resolve->{
            resolve.resolve(o);
        });
    }

    public static <E extends Throwable> Promise reject(E e) {
        return new Promise(resolve->{
            resolve.reject(e);
        });
    }

    public static Promise all(TaskCallback... taskCallbacks) {
        Object[] o = new Object[taskCallbacks.length];
        int[] a= new int[1];
        a[0]=0;
        for (TaskCallback taskCallback : taskCallbacks) {
            new Promise(taskCallback)
                .done(
                    r->{
                        o[a[0]]=r;
                    },
                    e->{
                        o[a[0]]=e;
                });
            a[0]++;
        }
        return new Promise(resolution->{
            resolution.resolve(o);
        });
    }

    private final TaskCallback taskCallback;
    private boolean hasMadeCall = false;
    private Resolution resolution;
    private final LockingMechanism lockingMechanism;

    public Promise(TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
        this.lockingMechanism = new LockingMechanism();
    }

    public <R> void doneResolve(ResultCallback<R> resultCallback) {
        done(resultCallback, e->{});
    }

    public <E extends Throwable> void doneReject(ErrorCallback<E> errorCallback) {
        done(input->{}, errorCallback);
    }

    public <R, E extends Throwable> void done(ResultCallback<R> resultCallback, ErrorCallback<E> errorCallback) {
        then(i->{
            resultCallback.call((R) i);
            return null;
        }, errorCallback);
    }

    public <E extends Throwable> Promise thenCatch(ErrorCallback<E> errorCallback) {
        return then(input-> input, errorCallback);
    }

    public <I, O> Promise thenTransform(TransformingCallback<I, O> transformingCallback) {
        //return then(transformingCallback, error->{});
        return then(transformingCallback, null);
    }

    public <I, O, E extends Throwable> Promise then(TransformingCallback<I, O> transformingCallback, ErrorCallback<E> errorCallback) {
        trace(()->format("Promise.then(TransformingCallback, ...) has made call %s ",hasMadeCall));
        if (!hasMadeCall) {
            resolution = new Resolution(lockingMechanism);
            try {
                lockingMechanism.lockAndWait(() -> {
                    trace(()->"perform taskCallback.call");
                    taskCallback.call(resolution);
                });
            } catch(InterruptedException e) {
                // todo: handle this, don't need to propagate through setInterrupted or rethrow InterruptedException
                // cause we own this
                logger.info(format("Thread %s interrupted ", Thread.currentThread(), e));
            } catch(Throwable t) {
                // todo: if TaskCallback throws exception, we treat it as rejected?
                logger.trace(format("Thread %s has throwable, treat as reject", Thread.currentThread(), t));
                resolution.reject(resolution.getResult(), t);
            }
        }

        if (resolution.isRejected()) {
            trace(()->"resolution is rejected");
            E e = (E) resolution.getException();
            if (errorCallback != null) {
                errorCallback.call(e);
                return new Promise(resolution1 -> {
                    resolution1.resolve(resolution.getResult()); // keep prev resolve
                });
            } else {
                return new Promise(resolution1->{
                    resolution1.reject(resolution.getResult(), e);
                });
            }
        } else {
            trace(()->"resolution is not rejected");
            O o = null;
            if (resolution.hasResult()) {
                trace(()->format("resolution has result %s", (Object)resolution.getResult()));
                try {
                    o = transformingCallback.call((I) resolution.getResult());
                    final O _o = o;
                    trace(() -> format("result transformed to %s", _o));
                } catch(Throwable t) {
                    return new Promise(resolution1 -> {
                        resolution1.reject(resolution.getResult(), t);
                    });
                }
            } else {
                trace(()->"resolution has no result");
                try {
                    o = transformingCallback.call(null);
                    final O _o = o;
                    trace(() -> format("result transformed to %s", _o));
                } catch(Throwable t) {
                    return new Promise(resolution1 -> {
                        resolution1.reject(resolution.getResult(), t);
                    });
                }
            }
            final O _o = o;
            return new Promise(resolution->{
                resolution.resolve(_o);
            });
        }
    }


    private void trace(Supplier<?> supplier) {
        if (logger.isTraceEnabled()) {
            logger.trace(format("(%s)   %s", this, supplier.get().toString()));
        }
    }
}
