package mjaruijs.homecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import mjaruijs.homecontrol.settings.Settings.KEYS.*
import java.lang.IllegalArgumentException

class Settings(context: Context) {

    var enableNotificationLighting = false

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun apply(value: Boolean) {
        enableNotificationLighting = preferences.getBoolean(ENABLE_NOTIFICATION_LIGHTING.id, value)
    }

    fun getBoolean(key: String): Boolean{
        return when (key) {
            ENABLE_NOTIFICATION_LIGHTING.id -> enableNotificationLighting
            else -> throw IllegalArgumentException("No such preference! $key")
        }
    }

    fun set(id: String, value: Boolean) {
        setBoolean(id, value)
        when (id) {
            ENABLE_NOTIFICATION_LIGHTING.id -> enableNotificationLighting = preferences.getBoolean(id, value)
        }
    }

    enum class KEYS constructor(val id: String) {
        ENABLE_NOTIFICATION_LIGHTING("led_strip_notification");

        override fun toString(): String {
            return id
        }
    }

}
