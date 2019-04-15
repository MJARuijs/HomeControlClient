package mjaruijs.homecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import mjaruijs.homecontrol.settings.Settings.KEYS.*
import java.lang.IllegalArgumentException

class Settings(context: Context) {

    var socketPower = false
    var pc = false
    var enableNotificationLighting = false
    var enableLedStrip1 = false
    var enableLedStrip2 = false

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun setString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun apply(value: Boolean) {
        socketPower = preferences.getBoolean(SOCKET_POWER.id, value)
        pc = preferences.getBoolean(PC_POWER.id, value)
        enableNotificationLighting = preferences.getBoolean(ENABLE_NOTIFICATION_LIGHTING.id, value)
        enableLedStrip1 = preferences.getBoolean(ENABLE_LED_STRIP_1.id, value)
        enableLedStrip2 = preferences.getBoolean(ENABLE_LED_STRIP_2.id, value)
    }

    fun getBoolean(key: String): Boolean{
        return when (key) {
            SOCKET_POWER.id -> socketPower
            PC_POWER.id -> pc
            ENABLE_NOTIFICATION_LIGHTING.id -> enableNotificationLighting
            ENABLE_LED_STRIP_1.id -> enableLedStrip1
            ENABLE_LED_STRIP_2.id -> enableLedStrip2
            else -> throw IllegalArgumentException("No such preference! $key")
        }
    }

    fun set(id: String, value: Boolean) {
        println("TRYING TO SET $id to $value")
        setBoolean(id, value)
        when (id) {
            SOCKET_POWER.id -> socketPower = preferences.getBoolean(id, value)
            PC_POWER.id -> pc = preferences.getBoolean(id, value)
            ENABLE_NOTIFICATION_LIGHTING.id -> enableNotificationLighting = preferences.getBoolean(id, value)
            ENABLE_LED_STRIP_1.id -> enableLedStrip1 = preferences.getBoolean(id, value)
            ENABLE_LED_STRIP_2.id -> enableLedStrip2 = preferences.getBoolean(id, value)
        }
    }

    enum class KEYS constructor(val id: String) {
        SOCKET_POWER("pc_socket_power"),
        PC_POWER("pc_power"),
        ENABLE_NOTIFICATION_LIGHTING("led_strip_notification"),
        ENABLE_LED_STRIP_1("led_strip_state1"),
        ENABLE_LED_STRIP_2("led_strip_state2");

        override fun toString(): String {
            return id
        }
    }

}
