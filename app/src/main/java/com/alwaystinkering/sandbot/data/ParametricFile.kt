package com.alwaystinkering.sandbot.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ParametricFile {

    @SerializedName("setup")
    @Expose
    var setup: String? = null
    @SerializedName("loop")
    @Expose
    var loop: String? = null
}
