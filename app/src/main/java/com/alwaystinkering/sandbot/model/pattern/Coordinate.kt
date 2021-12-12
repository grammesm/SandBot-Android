package com.alwaystinkering.sandbot.model.pattern

import java.util.*

class Coordinate(val x: Float, val y: Float) {

    override fun toString(): String {
        return String.format(Locale.US, "[%.05f, %.05f]", x, y)
    }
}
