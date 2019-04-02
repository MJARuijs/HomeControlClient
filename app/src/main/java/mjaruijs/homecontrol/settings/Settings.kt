package mjaruijs.homecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import mjaruijs.homecontrol.settings.Settings.KEYS.*

class Settings(context: Context) {

    var socketPower = false
    var pc = false
    var useMotionSensor = false
    var enableNotificationLighting = false
    var enableLedStrip = false

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun apply(configuration: BooleanArray) {
        if (configuration.size == 5) {
            socketPower = preferences.getBoolean(SOCKET_POWER.id, configuration[0])
            pc = preferences.getBoolean(PC_POWER.id, configuration[1])
            useMotionSensor = preferences.getBoolean(USE_MOTION_SENSOR.id, configuration[2])
            enableNotificationLighting = preferences.getBoolean(ENABLE_NOTIFICATION_LIGHTING.id, configuration[3])
            enableLedStrip = preferences.getBoolean(ENABLE_LED_STRIP.id, configuration[4])
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun init(configuration: BooleanArray) {
        if (configuration.size == 5) {
            setBoolean(SOCKET_POWER.id, configuration[0])
            setBoolean(PC_POWER.id, configuration[1])
            setBoolean(USE_MOTION_SENSOR.id, configuration[2])
            setBoolean(ENABLE_NOTIFICATION_LIGHTING.id, configuration[3])
            setBoolean(ENABLE_LED_STRIP.id, configuration[4])
        }
    }

    enum class KEYS constructor(val id: String) {
        SOCKET_POWER("socket_power"),
        PC_POWER("pc_power"),
        USE_MOTION_SENSOR("presence_detector"),
        ENABLE_NOTIFICATION_LIGHTING("led_strip"),
        ENABLE_LED_STRIP("led_strip_state");

        override fun toString(): String {
            return id
        }
    }

}
