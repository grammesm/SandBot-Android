package com.alwaystinkering.sandbot.data;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancellableCallback<T> implements Callback<T> {

    private Callback<T> callback;

    private boolean cancelled;

    private CancellableCallback() {}

    public CancellableCallback(Callback<T> callback) {
        this.callback = callback;
        cancelled = false;
    }

    public void cancel() {
        cancelled = true;
        callback = null;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!cancelled) {
            callback.onResponse(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!cancelled) {
            callback.onFailure(call, t);
        }
    }
}
