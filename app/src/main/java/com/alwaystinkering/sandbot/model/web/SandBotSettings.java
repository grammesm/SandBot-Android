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

public class SandBotSettings {

    private final static String TAG = "SandBotSettings";

    public interface ConfigWriteListener {
        void writeConfigResult(boolean success);
    }

    public void clear() {
    }


    public void parse(String jsonString) {

        try {
            JsonParser parser = new JsonParser();
            JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeConfig(final ConfigWriteListener listener) {
        String json = toJson().replaceAll("\\s+","");
        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), json);
        Call<ResponseBody> write = SandBotWeb.getInterface().postSettings(body);
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

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{}");


        return json.toString();
    }

    @Override
    public String toString() {
        return "SandBotSettings{" +
                '}';
    }
}
