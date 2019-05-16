package mjaruijs.homecontrol.services

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.settings.Settings

class QuickSettingsService : TileService() {

    private lateinit var settings: Settings
    private lateinit var enabledIcon: Icon
    private lateinit var disabledIcon: Icon

    override fun onCreate() {
        super.onCreate()
        settings = Settings(applicationContext)
        settings.apply(true)

        enabledIcon = Icon.createWithResource(this, R.drawable.synchronize_icon)
        disabledIcon = Icon.createWithResource(this, R.mipmap.lamp_icon)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        setTileState()
    }

    override fun onStartListening() {
        super.onStartListening()
        settings.apply(true)
        setTileState()
    }

    override fun onClick() {
        super.onClick()
        toggleTile()
        setTileState()
    }

    private fun setTileState() {
        val tile = qsTile

        if (settings.enableNotificationLighting) {
            tile.label = "LED enabled"
            tile.state = Tile.STATE_ACTIVE
            tile.icon = enabledIcon
        } else {
            tile.label = "LED disabled"
            tile.state = Tile.STATE_ACTIVE
            tile.icon = disabledIcon
        }

        tile.updateTile()
    }

    private fun toggleTile() {
        settings.set("led_strip_notification", !settings.enableNotificationLighting)
    }

}