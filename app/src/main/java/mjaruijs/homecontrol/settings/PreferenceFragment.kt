package mjaruijs.homecontrol.settings

import android.content.DialogInterface
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.dialogs.PowerWarningDialog
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.services.BroadCastReceiver
//import mjaruijs.homecontrol.services.NotificationListener
import java.util.regex.Pattern

class PreferenceFragment : android.preference.PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val className = "preference_fragment"

    private val broadcastReceiver = BroadCastReceiver(className, ::onReceive)
    private val filter = IntentFilter("mjaruijs.home_control.FROM_SERVER_TO_$className")
    private val pattern = Pattern.compile("(?<type>[_A-Z]*):\\[(?<values>.*)]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context.registerReceiver(broadcastReceiver, filter)
        settings = Settings(context)
        settings.apply(true)

        PreferenceManager.getDefaultSharedPreferences(activity.applicationContext).registerOnSharedPreferenceChangeListener(this)

        addPreferencesFromResource(R.xml.settings)

        setPreferenceListeners()
        synchronizeSettings()
    }

    override fun onResume() {
        super.onResume()
        context.registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    private fun unregisterReceiver() {
        try {
            context.unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            println("Receiver already unregistered!")
        }
    }

    fun synchronizeSettings() {
        NetworkManager.addMessage(className, "get_configuration")
    }

    private fun setPreferenceListeners() {
        findPreference("pc_socket_power").onPreferenceChangeListener = preferenceSwitchChangeListener
        findPreference("pc_power").onPreferenceChangeListener = preferenceSwitchChangeListener
        findPreference("led_strip_notification").onPreferenceChangeListener = preferenceSwitchChangeListener
//        findPreference("led_strip_effect").onPreferenceChangeListener = preferenceListChangeListener
        findPreference("led_strip_state1").onPreferenceChangeListener = preferenceSwitchChangeListener
        findPreference("led_strip_state2").onPreferenceChangeListener = preferenceSwitchChangeListener
    }

    private fun refreshSwitches() {
        (findPreference(Settings.KEYS.SOCKET_POWER.id) as SwitchPreference).isChecked = settings.socketPower
        (findPreference(Settings.KEYS.PC_POWER.id) as SwitchPreference).isChecked = settings.pc
        (findPreference(Settings.KEYS.ENABLE_NOTIFICATION_LIGHTING.id) as SwitchPreference).isChecked = settings.enableNotificationLighting
        (findPreference(Settings.KEYS.ENABLE_LED_STRIP_1.id) as SwitchPreference).isChecked = settings.enableLedStrip1
        (findPreference(Settings.KEYS.ENABLE_LED_STRIP_2.id) as SwitchPreference).isChecked = settings.enableLedStrip2
    }

    private fun enableAll() {
        (findPreference(Settings.KEYS.SOCKET_POWER.id) as SwitchPreference).isEnabled = true
        (findPreference(Settings.KEYS.PC_POWER.id) as SwitchPreference).isEnabled = true
        (findPreference(Settings.KEYS.ENABLE_NOTIFICATION_LIGHTING.id) as SwitchPreference).isEnabled = true
        (findPreference(Settings.KEYS.ENABLE_LED_STRIP_1.id) as SwitchPreference).isEnabled = true
        (findPreference(Settings.KEYS.ENABLE_LED_STRIP_2.id) as SwitchPreference).isEnabled = true
    }

    private val preferenceListChangeListener = OnPreferenceChangeListener { preference, newValue ->
        settings.setString(preference.key, newValue as String)
        true
    }

    private val preferenceSwitchChangeListener = OnPreferenceChangeListener { preference, _ ->
        val state = (findPreference(preference.key) as SwitchPreference).isChecked
        (findPreference(preference.key) as SwitchPreference).isChecked = !state
//        (findPreference(preference.key) as SwitchPreference).isEnabled = false

        val newState = if (state) "off" else "on"
        onClickCommand(preference.key, "${preference.key}_$newState")
        settings.set(preference.key, !state)
        true
    }

    private val onPowerClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
                settings.setBoolean("pc_socket_power", true)
                settings.setBoolean("pc_power", true)
                (findPreference("pc_socket_power") as SwitchPreference).isEnabled = true
                (findPreference("pc_socket_power") as SwitchPreference).isChecked = true
                (findPreference("pc_power") as SwitchPreference).isEnabled = true
                (findPreference("pc_power") as SwitchPreference).isChecked = true
            }

            DialogInterface.BUTTON_POSITIVE -> {
                onClickCommand("pc_socket_power", "confirm_socket_off")
            }
        }
    }

    private fun onReceive(message: String) {
        println(message)
        val subMessage = message.substring(message.indexOf(": ") + 2)
        val configs = subMessage.split(',')
        for (config in configs) {
            val matcher = pattern.matcher(config)
            if (matcher.matches()) {
                val type = matcher.group("type")
                val values = matcher.group("values").split('|')

                for (setting in values) {
                    if (setting == "PC_STILL_ON") {
                        powerWarningDialog.getDialog(context, onPowerClickListener).show()
                    } else {
                        try {
                            val separatorIndex = setting.indexOf('=')
                            val name = setting.substring(0, separatorIndex)
                            val value = setting.substring(separatorIndex + 1, setting.length)
                            val typeAbbreviation = type.removeSuffix("_CONTROLLER").toLowerCase()
                            settings.set(typeAbbreviation + "_$name", stringToBool(value))
                        } catch (e: Exception) {
                            println("FAILED FOR $setting")
                            continue
                        }
                    }
                }
            } else {
                println("REGEX FAILED FOR: $message")
            }
        }

        enableAll()
        refreshSwitches()
    }

    private fun stringToBool(value: String) = value == "on"

    private fun onClickCommand(vararg messages: String) {
        NetworkManager.addMessage(className, "StudyRoom|${messages.joinToString("|", "", "", -1, "", null)}")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
//        settings.set(key, sharedPreferences.getBoolean(key, false))
////        println(key)
//        (findPreference(key) as SwitchPreference).isChecked = settings.getBoolean(key)
    }

    companion object {
        private lateinit var settings: Settings
        private val powerWarningDialog = PowerWarningDialog
    }

}
