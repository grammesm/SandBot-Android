package com.alwaystinkering.sandbot.model.pattern;

import java.util.Locale;

public class Coordinate {

    private float x;
    private float y;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[%.05f, %.05f]", x, y);
    }
}
