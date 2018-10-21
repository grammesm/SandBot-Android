package com.alwaystinkering.sandbot.model.web;

public class Result {
    private String rslt;

    public String getRslt() {
        return rslt;
    }

    public void setRslt(String rslt) {
        this.rslt = rslt;
    }

    @Override
    public String toString() {
        return "Result: " + rslt;
    }
}
