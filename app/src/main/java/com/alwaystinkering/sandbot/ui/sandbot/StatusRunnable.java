package com.alwaystinkering.sandbot.ui.sandbot;

import android.util.Log;

import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusRunnable implements Runnable {
    private static final String TAG = "StatusRunnable";
    private boolean running = false;
    private MainActivity activity;

    public StatusRunnable(MainActivity activity) {
        this.activity = activity;
    }

    private void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            Call<ResponseBody> call = SandBotWeb.getInterface().getStatus();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String json = null;
                    try {
                        json = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Status JSON: " + json);
                    SandBotStateManager.getSandBotStatus().parse(json);
                    activity.refresh();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
