package mjaruijs.servertest

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import mjaruijs.servertest.Settings.KEYS.*

class Settings(context: Context) {

    var mainPower: Boolean = false
    var pc: Boolean = false
    var light: Boolean = false

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun apply(configuration: BooleanArray) {
        mainPower = preferences.getBoolean(SOCKET_POWER.toString(), configuration[0])
        pc = preferences.getBoolean(PC_POWER.toString(), configuration[1])
        light = preferences.getBoolean(LIGHT_ON.toString(), configuration[2])
    }

    fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun init(configuration: BooleanArray) {
        setBoolean(SOCKET_POWER.id, configuration[0])
        setBoolean(PC_POWER.id, configuration[1])
        setBoolean(LIGHT_ON.id, configuration[2])
    }

    enum class KEYS constructor(val id: String) {
        PC_POWER("pc_power"),
        SOCKET_POWER("socket_power"),
        LIGHT_ON("light_on");

        override fun toString(): String {
            return id
        }
    }

}
