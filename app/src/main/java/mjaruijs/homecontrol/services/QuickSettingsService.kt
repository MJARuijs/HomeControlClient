package mjaruijs.homecontrol.services

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.settings.Settings

class QuickSettingsService : TileService() {

    private lateinit var settings: Settings
//    private lateinit var notificationListener: Intent

    override fun onCreate() {
        super.onCreate()
        settings = Settings(applicationContext)
        settings.apply(true)
//        notificationListener = Intent(applicationContext, NotificationListener::class.java)
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
        println("CLICKED ${settings.enableNotificationLighting}")

    }

    private fun setTileState() {
        val tile = qsTile

        if (settings.enableNotificationLighting) {
            tile.label = "LED enabled"
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(this, R.drawable.synchronize_icon)
//            startService(notificationListener)
        } else {
            tile.label = "LED disabled"
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(this, R.mipmap.lamp_icon)
//            stopService(notificationListener)
        }

        tile.updateTile()
    }

    private fun toggleTile() {
        settings.set("led_strip_notification", !settings.enableNotificationLighting)
    }

}