package com.alwaystinkering.sandbot.model.web;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SandBotStatus {

    private static final String TAG = "SandBotStatus";

    public interface ConfigWriteListener {
        void writeConfigResult(boolean success);
    }


    private boolean ledOn;
    private int ledBrightness;
    private boolean ledAutoDim;
    private int speed;


    public int getLedBrightness() {
        return ledBrightness;
    }

    public void setLedBrightness(int ledBrightness) {
        this.ledBrightness = ledBrightness;
    }

    public boolean isLedAutoDim() {
        return ledAutoDim;
    }

    public void setLedAutoDim(boolean ledAutoDim) {
        this.ledAutoDim = ledAutoDim;
    }

    public boolean isLedOn() {
        return ledOn;
    }

    public void setLedOn(boolean ledOn) {
        this.ledOn = ledOn;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String ledJson() {
        return "{\"ledOn\":" + (ledOn ? "1" : "0") + ",\"ledValue\":" + ledBrightness + ",\"autoDim\":" + (ledAutoDim ? "1" : "0") + "}";
    }

    public void writeLedConfig(final ConfigWriteListener listener) {
        String json = ledJson().replaceAll("\\s+","");
        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), json);
        Call<ResponseBody> write = SandBotWeb.getInterface().setLed(body);
        write.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d(TAG, "Write success: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listener.writeConfigResult(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Write FAIL: " + t.getLocalizedMessage());
                listener.writeConfigResult(false);
            }
        });

    }

    public void parse(String jsonString) {

        try {
            JsonParser parser = new JsonParser();
            JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();
            setLedOn(rootObj.getAsJsonPrimitive("ledOn").getAsInt() == 1);
            setLedBrightness(rootObj.getAsJsonPrimitive("ledValue").getAsInt());
            setLedAutoDim(rootObj.getAsJsonPrimitive("autoDim").getAsInt() == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
