package com.alwaystinkering.sandbot.model.web;

import android.util.Log;

import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.alwaystinkering.sandbot.model.sequence.Sequence;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
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
    private Map<String, Sequence> sequences = new HashMap<>();
    private String startup;

    private int ledBrightness;
    private boolean ledAutoDim;
    private int speed;

    public Integer getMaxCfgLen() {
        return maxCfgLen;
    }

    public void clear() {
        name = "";
        patterns.clear();
        sequences.clear();
        startup = "";
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

    public Map<String, Sequence> getSequences() {
        return sequences;
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

            JsonObject sequencesObject = rootObj.getAsJsonObject("sequences");
            entries = sequencesObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                String name = entry.getKey();
                JsonObject sequenceObject = sequencesObject.getAsJsonObject(name);
                String commands = sequenceObject.getAsJsonPrimitive("commands").getAsString();
                JsonPrimitive runAtStartPrim = sequenceObject.getAsJsonPrimitive("runAtStart");
                int runAtStart = 0;
                if (runAtStartPrim != null) {
                    runAtStart = runAtStartPrim.getAsInt();
                }

                if (sequences.containsKey(name)) {
                    sequences.get(name).setCommands(commands);
                    sequences.get(name).setAutoRun(runAtStart == 1);
                } else {
                    sequences.put(name, new Sequence(name, commands, runAtStart == 1));
                }
//                if (startup.equals(name)) {
//                    sequences.get(name).setAutoRun(true);
//                } else {
//                    sequences.get(name).setAutoRun(false);
//                }
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();

        json.append("{")
                .append("\"maxCfgLen\":").append(String.valueOf(maxCfgLen)).append(",")
                .append("\"name\":\"").append(name).append("\",")
                .append("\"patterns\":")
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
                .append("\"sequences\":")
                .append("{");
        count = 1;
        for (Sequence sequence : sequences.values()) {
            if (sequence.isAutoRun()) {
                setStartup("g28;" + sequence.getName());
            }
            json.append(sequence.toJson());
            if (count != sequences.size()) {
                json.append(",\n");
            }
            count++;
        }
        json.append("},")
                .append("\"startup\":\"").append(startup).append("\"")
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
