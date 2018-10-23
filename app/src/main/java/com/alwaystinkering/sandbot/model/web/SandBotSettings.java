package com.alwaystinkering.sandbot.model.web;

import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SandBotSettings implements Serializable {
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
        System.out.println(");");
    }

    public void writeConfig() {

    }

    public String toJson() {
        StringBuilder json = new StringBuilder();

        json.append("{\n")
                .append("\t\"maxCfgLen\":").append(String.valueOf(maxCfgLen)).append("\n")
                .append("\t\"name\":\"").append(name).append("\"\n")
                .append("\t\"patterns\":").append("\n")
                .append("\t{");
        int count = 1;
        for (Pattern pattern : patterns.values()) {
            json.append(pattern.toJson());
            if (count != patterns.size()) {
                json.append(",\n");
            }
            count++;
        }
        json.append("\t},\n")
                .append("\t\"sequences\":").append("\n")
                .append("\t{\n");
//        for (Sequence sequence : sequences.values()) {
//        json.append(sequence.toJson());
//        if (count != sequences.size()) {
//            json.append(",\n");
//        }
//        count++;
//        }
        json.append("\t},\n")
                .append("\t\"startup\":\"").append(startup).append("\"\n")
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
