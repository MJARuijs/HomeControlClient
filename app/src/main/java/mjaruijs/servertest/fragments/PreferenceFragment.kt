package mjaruijs.servertest.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.SwitchPreference
import android.widget.Toast
import mjaruijs.servertest.R
import mjaruijs.servertest.Settings
import mjaruijs.servertest.activities.login.LoginActivity
import mjaruijs.servertest.server.ConfigConnection
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.command.CommandConnection
import mjaruijs.servertest.server.command.CommandResult
import mjaruijs.servertest.server.command.CommandResult.*

class PreferenceFragment : android.preference.PreferenceFragment() {

    private val preferenceChangeListener = OnPreferenceChangeListener { preference, newState ->
        val state = (findPreference(preference.key) as SwitchPreference).isChecked
        (findPreference(preference.key) as SwitchPreference).isEnabled = false
         CommandConnection(object : ConnectionResponse() {
             override fun commandResult(result: CommandResult) {
                 super.commandResult(result)
                 when (result) {
                     SUCCESS -> {
                         settings.setBoolean(preference.key, newState as Boolean)
                         (findPreference(preference.key) as SwitchPreference).isEnabled = true
                         (findPreference(preference.key) as SwitchPreference).isChecked = newState
                     }
                     SESSION_EXPIRED -> {
                         Toast.makeText(context, "Session expired!", Toast.LENGTH_SHORT).show()
                         startActivity(Intent(context, LoginActivity::class.java))
                     }
                     PC_STILL_ON -> {
                         Toast.makeText(context, "PC is already running!", Toast.LENGTH_SHORT).show()
                         settings.setBoolean(preference.key, true)
                         (findPreference(preference.key) as SwitchPreference).isChecked = true
                         (findPreference(preference.key) as SwitchPreference).isEnabled = true
                     }
                     else -> {
                     }
                 }

             }
         }).execute(preference.key, state.toString())

        true
    }

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
    }

    private fun refreshSwitches() {
        (findPreference(Settings.KEYS.SOCKET_POWER.id) as SwitchPreference).isChecked = settings.mainPower
        (findPreference(Settings.KEYS.PC_POWER.id) as SwitchPreference).isChecked = settings.pc
        (findPreference(Settings.KEYS.LIGHT.id) as SwitchPreference).isChecked = settings.light
    }

    companion object {
        private val TAG = "PreferenceFragment"
        private lateinit var settings: Settings
    }

}
