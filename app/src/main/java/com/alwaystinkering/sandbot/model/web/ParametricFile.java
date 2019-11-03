package com.alwaystinkering.sandbot.model.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParametricFile {

    @SerializedName("setup")
    @Expose
    private String setup;
    @SerializedName("loop")
    @Expose
    private String loop;

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }
}
