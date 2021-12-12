package com.alwaystinkering.sandbot.api

class CommandResult {
    var rslt: String? = null

    override fun toString(): String {
        return "Result: " + rslt!!
    }
}
