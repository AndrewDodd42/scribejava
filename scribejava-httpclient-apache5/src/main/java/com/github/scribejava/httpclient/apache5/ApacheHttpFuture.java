package com.github.scribejava.httpclient.apache5;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class ApacheHttpFuture<T> implements Future<T> {

    private final Future<ResponseWithEntity> future;
    private final OAuthAsyncCompletionHandler<T> handler;

    ApacheHttpFuture(Future<ResponseWithEntity> future, OAuthAsyncCompletionHandler<T> handler) {
        this.future = future;
        this.handler = handler;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return handler.getResult();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return handler.getResult(timeout, unit);
    }
}
