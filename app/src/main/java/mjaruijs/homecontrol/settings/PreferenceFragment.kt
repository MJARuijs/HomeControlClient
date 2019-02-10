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
import mjaruijs.homecontrol.networking.server.ConfigConnection
import mjaruijs.homecontrol.networking.server.ConnectionResponse
import mjaruijs.homecontrol.networking.server.command.CommandConnection
import mjaruijs.homecontrol.networking.server.command.CommandResult.*

class PreferenceFragment : android.preference.PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.settings)
        settings = Settings(activity.applicationContext)

        setPreferenceListeners()
        synchronizeSettings()
    }

    fun synchronizeSettings() {
        ConfigConnection(object : ConnectionResponse() {
            override fun resultBooleanArray(result: BooleanArray) {
                super.resultBooleanArray(result)
                settings.init(result)
                settings.apply(result)
                refreshSwitches()
            }
        }).execute("get_configuration")
    }

    private fun setPreferenceListeners() {
        findPreference("pc_power").onPreferenceChangeListener = preferenceChangeListener
        findPreference("socket_power").onPreferenceChangeListener = preferenceChangeListener
        findPreference("light").onPreferenceChangeListener = preferenceChangeListener
        findPreference("use_motion_sensor").onPreferenceChangeListener = preferenceChangeListener
        findPreference("enable_notification_lighting").onPreferenceChangeListener = preferenceChangeListener
    }

    private fun refreshSwitches() {
        (findPreference(Settings.KEYS.SOCKET_POWER.id) as SwitchPreference).isChecked = settings.socketPower
        (findPreference(Settings.KEYS.PC_POWER.id) as SwitchPreference).isChecked = settings.pc
        (findPreference(Settings.KEYS.LIGHT.id) as SwitchPreference).isChecked = settings.light
        (findPreference(Settings.KEYS.USE_MOTION_SENSOR.id) as SwitchPreference).isChecked = settings.useMotionSensor
        (findPreference(Settings.KEYS.ENABLE_NOTIFICATION_LIGHTING.id) as SwitchPreference).isChecked = settings.enableNotificationLighting
    }

    private val preferenceChangeListener = OnPreferenceChangeListener { preference, _ ->
        val state = (findPreference(preference.key) as SwitchPreference).isChecked
        (findPreference(preference.key) as SwitchPreference).isEnabled = false
        CommandConnection(object : ConnectionResponse() {
            override fun config(result: Any) {
                super.config(result)
                if (result is BooleanArray) {
                    settings.init(result)
                    settings.apply(result)
                    refreshSwitches()
                } else {
                    when (result) {
                        SESSION_EXPIRED -> {
                            Toast.makeText(context, "Session expired!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, LoginActivity::class.java))
                        }
                        PC_STILL_ON -> {
                            if (preference.key == "pc_power") {
                                powerWarningDialog.getDialog(context, onPcClickListener).show()
                            } else {
                                powerWarningDialog.getDialog(context, onPowerClickListener).show()
                            }
                        }
                        else -> {}
                    }
                }
                (findPreference(preference.key) as SwitchPreference).isEnabled = true
            }
        }).execute(preference.key, state.toString())

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
                CommandConnection(object : ConnectionResponse() {
                    override fun config(result: Any) {
                        super.config(result)
                        if (result is BooleanArray) {
                            settings.init(result)
                            settings.apply(result)
                            refreshSwitches()
                        }
                    }
                }).execute("confirm_pc_shutdown")
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
                CommandConnection(object : ConnectionResponse() {
                    override fun config(result: Any) {
                        super.config(result)
                        if (result is BooleanArray) {
                            settings.init(result)
                            settings.apply(result)
                            refreshSwitches()
                        }
                    }
                }).execute("confirm_socket_off")
            }
        }
    }

    companion object {
        private lateinit var settings: Settings
        private val powerWarningDialog = PowerWarningDialog
    }

}
