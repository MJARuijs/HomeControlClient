package mjaruijs.homecontrol.services

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.widget.Toast
import mjaruijs.homecontrol.activities.lampsetup.data.Data
import mjaruijs.homecontrol.activities.lampsetup.data.cards.AppCardList
import mjaruijs.homecontrol.colorpicker.Color
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.networking.server.NotificationConnection

class NotificationListener : NotificationListenerService() {

    private val color = Color(0, 0.0f, 1.0f, 0.0f)
    private val resetColor = Color(0, -1.0f, 0.0f, 0.0f)
    private val cards = Data.getCards()

    private lateinit var apps: List<PackageInfo>

    override fun onCreate() {
        super.onCreate()
        apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val appName = getAppName(sbn.packageName) ?: throw IllegalArgumentException()

        if (NetworkManager.isConnectedToHomeNetwork(applicationContext)) {

            val card = AppCardList.cards.find { card -> card.appName == appName } ?: return
            NotificationConnection().execute(card.color.getHSL(254f))
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val appName = getAppName(sbn.packageName) ?: throw IllegalArgumentException()

        if (NetworkManager.isConnectedToHomeNetwork(applicationContext) && sbn.packageName.contains("mjaruijs")) {
            AppCardList.cards.find { card -> card.appName == appName } ?: return
            NotificationConnection().execute(resetColor.getHSL(-1.0f))
        }
    }

    private fun getAppName(packageName: String): String? {
        for (pInfo in apps) {
            if (pInfo.applicationInfo.packageName == packageName) {
                return pInfo.applicationInfo.loadLabel(packageManager).toString()
            }
        }
        return null
    }

}