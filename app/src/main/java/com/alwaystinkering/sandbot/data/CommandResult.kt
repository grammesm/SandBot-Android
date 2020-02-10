package com.alwaystinkering.sandbot.data

class CommandResult {
    var rslt: String? = null

    override fun toString(): String {
        return "Result: " + rslt!!
    }
}
