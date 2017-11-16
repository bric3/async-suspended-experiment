package com.github.bric3.asyncsuspendedtest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CountDownLatch;

class HttpCallCallback implements Callback {
    private Response response;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void onFailure(Call call, IOException e) {
        throw new UncheckedIOException(e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        latch.countDown();
        this.response = response;
    }

    public HttpCallCallback enqueue(Call httpCall) {
        httpCall.enqueue(this);
        return this;
    }

    public Response response() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return response;
    }

    public boolean hasResponse() {
        return response != null;
    }
}
