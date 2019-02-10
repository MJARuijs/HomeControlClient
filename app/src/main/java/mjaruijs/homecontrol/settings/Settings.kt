package mjaruijs.homecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import mjaruijs.homecontrol.settings.Settings.KEYS.*

class Settings(context: Context) {

    var socketPower = false
    var pc = false
    var light = false
    var useMotionSensor = false
    var enableNotificationLighting = false

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun apply(configuration: BooleanArray) {
        if (configuration.size == 4) {
            socketPower = preferences.getBoolean(SOCKET_POWER.id, configuration[0])
            pc = preferences.getBoolean(PC_POWER.id, configuration[1])
            light = preferences.getBoolean(LIGHT.id, configuration[2])
            useMotionSensor = preferences.getBoolean(USE_MOTION_SENSOR.id, configuration[3])
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun init(configuration: BooleanArray) {
        if (configuration.size == 4) {
            setBoolean(SOCKET_POWER.id, configuration[0])
            setBoolean(PC_POWER.id, configuration[1])
            setBoolean(LIGHT.id, configuration[2])
            setBoolean(USE_MOTION_SENSOR.id, configuration[3])
        }
    }

    enum class KEYS constructor(val id: String) {
        SOCKET_POWER("socket_power"),
        PC_POWER("pc_power"),
        LIGHT("light"),
        USE_MOTION_SENSOR("use_motion_sensor"),
        ENABLE_NOTIFICATION_LIGHTING("enable_notification_lighting");

        override fun toString(): String {
            return id
        }
    }

}
