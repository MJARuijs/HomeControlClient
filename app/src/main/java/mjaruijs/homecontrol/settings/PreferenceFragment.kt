package mjaruijs.homecontrol.settings

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.SwitchPreference
import android.widget.Toast
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.LoginActivity
import mjaruijs.homecontrol.activities.dialogs.PowerWarningDialog
import mjaruijs.homecontrol.networking.MessageSender

class PreferenceFragment : android.preference.PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.settings)
        settings = Settings(activity.applicationContext)

        setPreferenceListeners()
        synchronizeSettings()
    }

    fun synchronizeSettings() {
        MessageSender(object : MessageSender.ConnectionResponse {
            override fun result(message: String) {
                println(message)
                val config = parseConfig(message)
                settings.init(config.first)
                settings.apply(config.first)
                refreshSwitches()
            }
        }).execute("get_configuration")
    }

    private fun setPreferenceListeners() {
        findPreference("socket_power").onPreferenceChangeListener = preferenceChangeListener
        findPreference("pc_power").onPreferenceChangeListener = preferenceChangeListener
        findPreference("presence_detector").onPreferenceChangeListener = preferenceChangeListener
        findPreference("led_strip").onPreferenceChangeListener = preferenceChangeListener
        findPreference("led_strip_state").onPreferenceChangeListener = preferenceChangeListener
    }

    private fun refreshSwitches() {
        (findPreference(Settings.KEYS.SOCKET_POWER.id) as SwitchPreference).isChecked = settings.socketPower
        (findPreference(Settings.KEYS.PC_POWER.id) as SwitchPreference).isChecked = settings.pc
        (findPreference(Settings.KEYS.USE_MOTION_SENSOR.id) as SwitchPreference).isChecked = settings.useMotionSensor
        (findPreference(Settings.KEYS.ENABLE_NOTIFICATION_LIGHTING.id) as SwitchPreference).isChecked = settings.enableNotificationLighting
        (findPreference(Settings.KEYS.ENABLE_LED_STRIP.id) as SwitchPreference).isChecked = settings.enableLedStrip
    }

    private fun parseConfig(message: String): Pair<BooleanArray, String> {
        val values = message.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val config = BooleanArray(values.size)

        for (value in values) {

            val setting = value.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val key = setting[0]

            when (key) {
                "socket_power" -> config[0] = getBooleanValue(setting[1])
                "pc_power" -> config[1] = getBooleanValue(setting[1])
                "presence_detector" -> config[2] = getBooleanValue(setting[1])
                "led_strip" -> config[3] = getBooleanValue(setting[1])
                "led_strip_state" -> config[4] = getBooleanValue(setting[1])
            }
        }

        return Pair(config, message)
    }

    private fun getBooleanValue(input: String): Boolean {
        return input == "true"
    }

    private val preferenceChangeListener = OnPreferenceChangeListener { preference, _ ->
        val state = (findPreference(preference.key) as SwitchPreference).isChecked
        (findPreference(preference.key) as SwitchPreference).isChecked = !state
        (findPreference(preference.key) as SwitchPreference).isEnabled = false

        val newState = if (state) "off" else "on"

        onClickCommand(preference.key, "${preference.key}_$newState") { message ->
            val config = parseConfig(message)

            println(config.second)
            if (config.first.size != 5) {
                when (config.second) {
                    "SESSION_EXPIRED" -> {
                        Toast.makeText(context, "Session expired!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                    "PC_STILL_ON" -> {
                        if (preference.key == "pc_power") {
                            powerWarningDialog.getDialog(context, onPcClickListener).show()
                        } else {
                            powerWarningDialog.getDialog(context, onPowerClickListener).show()
                        }
                    }
                    else -> {}
                }
            } else {
                println("dafuq")
                settings.init(config.first)
                settings.apply(config.first)
                refreshSwitches()
            }

            (findPreference(preference.key) as SwitchPreference).isEnabled = true
        }

        true
    }

    private val onPcClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
                settings.setBoolean("pc_power", true)
                (findPreference("pc_power") as SwitchPreference).isEnabled = true
                (findPreference("pc_power") as SwitchPreference).isChecked = true
            }

            DialogInterface.BUTTON_POSITIVE -> {
                onClickCommand("pc_power", "confirm_pc_shutdown") { message ->
                    val config = parseConfig(message)
                    settings.init(config.first)
                    settings.apply(config.first)
                    refreshSwitches()
                }
            }
        }
    }

    private val onPowerClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
                settings.setBoolean("socket_power", true)
                (findPreference("socket_power") as SwitchPreference).isEnabled = true
                (findPreference("socket_power") as SwitchPreference).isChecked = true
            }

            DialogInterface.BUTTON_POSITIVE -> {
                onClickCommand("socket_power", "confirm_socket_off") { message ->
                    val config = parseConfig(message)
                    settings.init(config.first)
                    settings.apply(config.first)
                    refreshSwitches()
                }
            }
        }
    }

    private fun onClickCommand(vararg messages: String, function : (String) -> Unit) {
        MessageSender(object : MessageSender.ConnectionResponse {
            override fun result(message: String) {
                function(message)
            }
        }).execute("StudyRoom", *messages)
    }

    companion object {
        private lateinit var settings: Settings
        private val powerWarningDialog = PowerWarningDialog
    }

}
