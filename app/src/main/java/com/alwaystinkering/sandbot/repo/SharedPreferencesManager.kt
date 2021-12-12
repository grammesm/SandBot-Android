package com.alwaystinkering.sandbot.repo

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.alwaystinkering.sandbot.R
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(val context: Context) {

    var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getServeIpAddress(): String {
        return sharedPreferences.getString(
            context.getString(R.string.pref_key_server_ip),
            context.getString(R.string.pref_server_default)
        )!!
    }

    fun getTableDiameter(): Int {
        return sharedPreferences.getString(context.getString(R.string.pref_key_diameter), "400")!!.toInt()
    }
}