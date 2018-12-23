package com.alwaystinkering.sandbot.model.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SandBotFile {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("size")
    @Expose
    private Integer size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SandBotFile{" +
                "name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
