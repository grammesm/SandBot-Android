package com.alwaystinkering.sandbot.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SandBotFile {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("size")
    @Expose
    var size: Int? = null

    override fun toString(): String {
        return "SandBotFile{" +
                "name='" + name + '\''.toString() +
                ", size=" + size +
                '}'.toString()
    }
}
