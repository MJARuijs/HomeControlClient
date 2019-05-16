package mjaruijs.homecontrol.settings

import android.content.DialogInterface
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.dialogs.PowerWarningDialog
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.services.BroadCastReceiver
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
    }

    override fun onResume() {
        super.onResume()
        context.registerReceiver(broadcastReceiver, filter)
        synchronizeSettings()
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
        (findPreference("pc_socket_power") as SwitchPreference).setText("Socket Power")
        (findPreference("pc_socket_power") as SwitchPreference).onClickListener = {

        }

        (findPreference("pc_power") as SwitchPreference).setText("PC Power")
        (findPreference("pc_power") as SwitchPreference).onClickListener = {

        }

        (findPreference("led_strip_notification") as SwitchPreference).setText("Flash strips upon getting notifications")
        (findPreference("led_strip_notification") as SwitchPreference).onClickListener = {
            val state = (findPreference("led_strip_notification") as SwitchPreference).isChecked
            settings.set("led_strip_notification", state)
        }

        (findPreference("led_strip_state1") as ClickableSwitchPreference).setText("LedStrip 1")
        (findPreference("led_strip_state1") as ClickableSwitchPreference).onClickListener = {
            val state = (findPreference("led_strip_state1") as ClickableSwitchPreference).isChecked
            val newState = if (state) "on" else "off"
            onClickCommand("led_strip_state1", "led_strip_state1_$newState")
        }

        (findPreference("led_strip_state2") as ClickableSwitchPreference).setText("LedStrip 2")
        (findPreference("led_strip_state2") as ClickableSwitchPreference).onClickListener = {
            val state = (findPreference("led_strip_state2") as ClickableSwitchPreference).isChecked
            val newState = if (state) "on" else "off"
            onClickCommand("led_strip_state2", "led_strip_state2_$newState")
        }
    }

    private fun refreshSwitches() {
        (findPreference(Settings.KEYS.ENABLE_NOTIFICATION_LIGHTING.id) as SwitchPreference).isChecked = (settings.enableNotificationLighting)
    }

    private val onPowerClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
                settings.setBoolean("pc_socket_power", true)
                settings.setBoolean("pc_power", true)
                (findPreference("pc_socket_power") as SwitchPreference).isEnabled = true
                (findPreference("pc_socket_power") as SwitchPreference).isChecked = (true)
                (findPreference("pc_power") as SwitchPreference).isEnabled = true
                (findPreference("pc_power") as SwitchPreference).isChecked = (true)
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
//                            (findPreference("${typeAbbreviation}_$name") as ClickableSwitchPreference)
                            when {
                                name.contains("state1") -> (findPreference("led_strip_state1") as SwitchPreference).isChecked = (stringToBool(value))
                                name.contains("state2") -> (findPreference("led_strip_state2") as SwitchPreference).isChecked = (stringToBool(value))
                                name.contains("socket_power") -> (findPreference("pc_socket_power") as SwitchPreference).isChecked = (stringToBool(value))
                                name.contains("power") -> (findPreference("pc_power") as SwitchPreference).isChecked = (stringToBool(value))
                                else -> settings.set(typeAbbreviation + "_$name", stringToBool(value))
                            }
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

        refreshSwitches()
    }

    private fun stringToBool(value: String) = value == "on"

    private fun onClickCommand(vararg messages: String) {
        NetworkManager.addMessage(className, "StudyRoom|${messages.joinToString("|", "", "", -1, "", null)}")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        settings.set(key, sharedPreferences.getBoolean(key, false))
        (findPreference(key) as SwitchPreference).isChecked = (settings.getBoolean(key))
    }

    companion object {
        private lateinit var settings: Settings
        private val powerWarningDialog = PowerWarningDialog
    }

}
