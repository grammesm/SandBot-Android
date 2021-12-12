package com.alwaystinkering.sandbot.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FileListResult {
    @SerializedName("rslt")
    @Expose
    var rslt: String? = null
    @SerializedName("fsName")
    @Expose
    var fsName: String? = null
    @SerializedName("fsBase")
    @Expose
    var fsBase: String? = null
    @SerializedName("diskSize")
    @Expose
    var diskSize: Long? = null
    @SerializedName("diskUsed")
    @Expose
    var diskUsed: Long? = null
    @SerializedName("folder")
    @Expose
    var folder: String? = null
    @SerializedName("files")
    @Expose
    var sandBotFiles: List<SandBotFile>? = null

    override fun toString(): String {
        return "FileListResult{" +
                "rslt='" + rslt + '\''.toString() +
                ", fsName='" + fsName + '\''.toString() +
                ", fsBase='" + fsBase + '\''.toString() +
                ", diskSize=" + diskSize +
                ", diskUsed=" + diskUsed +
                ", folder='" + folder + '\''.toString() +
                ", sandBotFiles=" + sandBotFiles +
                '}'.toString()
    }
}
