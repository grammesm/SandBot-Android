package com.alwaystinkering.sandbot.model.web;

import android.util.Log;

import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private int maxCfgLen;
    private String name;
    private Map<String, Pattern> patterns = new HashMap<>();
    //private Sequences sequences;
    private String startup;

    public Integer getMaxCfgLen() {
        return maxCfgLen;
    }

    public void setMaxCfgLen(Integer maxCfgLen) {
        this.maxCfgLen = maxCfgLen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Pattern> getPatterns() {
        return patterns;
    }

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    public void parse(String jsonString) {

        try {
            JsonParser parser = new JsonParser();
            JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();
            setMaxCfgLen(rootObj.getAsJsonPrimitive("maxCfgLen").getAsInt());
            setName(rootObj.getAsJsonPrimitive("name").getAsString());
            setStartup(rootObj.getAsJsonPrimitive("startup").getAsString());
            JsonObject patternsObject = rootObj.getAsJsonObject("patterns");
            Set<Map.Entry<String, JsonElement>> entries = patternsObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                String name = entry.getKey();
                JsonObject patternObject = patternsObject.getAsJsonObject(name);
                String setup = patternObject.getAsJsonPrimitive("setup").getAsString();
                String loop = patternObject.getAsJsonPrimitive("loop").getAsString();

                if (patterns.containsKey(name)) {
                    patterns.get(name).setDeclarationString(setup);
                    patterns.get(name).setExpressionString(loop);
                } else {
                    patterns.put(name, new Pattern(name, setup, loop));
                }
            }
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

        json.append("{")
                .append("\"maxCfgLen\":").append(String.valueOf(maxCfgLen)).append(",")
                .append("\"name\":\"").append(name).append("\",")
                .append("\"patterns\":").append("")
                .append("{");
        int count = 1;
        for (Pattern pattern : patterns.values()) {
            json.append(pattern.toJson());
            if (count != patterns.size()) {
                json.append(",");
            }
            count++;
        }
        json.append("},")
                .append("\"sequences\":").append("")
                .append("{");
//        for (Sequence sequence : sequences.values()) {
//        json.append(sequence.toJson());
//        if (count != sequences.size()) {
//            json.append(",\n");
//        }
//        count++;
//        }
        json.append("},")
                .append("\"startup\":\"")./*append(startup).*/append("\"")
                .append("}");

        return json.toString();
    }

    @Override
    public String toString() {
        return "SandBotSettings{" +
                "maxCfgLen=" + maxCfgLen +
                ", name='" + name + '\'' +
                ", patterns=" + patterns +
                ", startup='" + startup + '\'' +
                '}';
    }
}
