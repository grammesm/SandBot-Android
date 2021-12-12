package com.alwaystinkering.sandbot.model

import com.alwaystinkering.sandbot.api.BotStatus

class LedState(status: BotStatus) {

    var ledOn: Int? = null
    var ledValue: Int? = null
    var autoDim: Int? = null

    init {
        this.ledOn = status.ledOn
        this.ledValue = status.ledValue
        this.autoDim = status.autoDim
    }

    fun ledJson(): String {
        return "{\"ledOn\":$ledOn,\"ledValue\":$ledValue,\"autoDim\":$autoDim}"
    }

}
