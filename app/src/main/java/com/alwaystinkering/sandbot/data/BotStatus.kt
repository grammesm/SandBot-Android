package com.alwaystinkering.sandbot.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BotStatus {

    @SerializedName("wifiIP")
    @Expose
    var wifiIP: String? = null
    @SerializedName("wifiConn")
    @Expose
    var wifiConn: String? = null
    @SerializedName("ssid")
    @Expose
    var ssid: String? = null
    @SerializedName("MAC")
    @Expose
    var mac: String? = null
    @SerializedName("RSSI")
    @Expose
    var rssi: Int? = null
    @SerializedName("XYZ")
    @Expose
    var xyz: List<Double>? = null
    @SerializedName("ABC")
    @Expose
    var abc: List<Int>? = null
    @SerializedName("mv")
    @Expose
    var mv: String? = null
    @SerializedName("end")
    @Expose
    var end: List<List<Int>>? = null
    @SerializedName("OoB")
    @Expose
    var ooB: String? = null
    @SerializedName("num")
    @Expose
    var num: Int? = null
    @SerializedName("Qd")
    @Expose
    var qd: Int? = null
    @SerializedName("Hmd")
    @Expose
    var hmd: Int? = null
    @SerializedName("pause")
    @Expose
    var pause: Int? = null
    @SerializedName("ledOn")
    @Expose
    var ledOn: Int? = null
    @SerializedName("ledValue")
    @Expose
    var ledValue: Int? = null
    @SerializedName("autoDim")
    @Expose
    var autoDim: Int? = null
    @SerializedName("tod")
    @Expose
    var tod: String? = null

    override fun toString(): String {
        return "BotStatus{" +
                "wifiIP='" + wifiIP + '\''.toString() +
                ", wifiConn='" + wifiConn + '\''.toString() +
                ", rSSI=" + rssi +
                ", num=" + num +
                ", qd=" + qd +
                ", pause=" + pause +
                ", ledOn=" + ledOn +
                ", ledValue=" + ledValue +
                ", autoDim=" + autoDim +
                ", tod='" + tod + '\''.toString() +
                '}'.toString()
    }
}