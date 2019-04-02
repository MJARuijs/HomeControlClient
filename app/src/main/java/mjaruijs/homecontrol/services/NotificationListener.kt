package mjaruijs.homecontrol.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.data.Data
import mjaruijs.homecontrol.activities.lampsetup.data.cards.AppCardList
import mjaruijs.homecontrol.colorpicker.Color
import mjaruijs.homecontrol.networking.MessageSender
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.networking.server.NotificationConnection

class NotificationListener : NotificationListenerService() {

    private val color = Color(0, 0.0f, 1.0f, 0.0f)
    private val resetColor = Color(0, -1.0f, 0.0f, 0.0f)
    private val cards = Data.getCards()

    private lateinit var vibrator: Vibrator

    private lateinit var apps: List<PackageInfo>

    override fun onCreate() {
        super.onCreate()
        apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == "mjaruijs.homecontrol") return

//        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        val notification = NotificationCompat.Builder(applicationContext, "HOME_CONTROL")
                .setSmallIcon(R.drawable.synchronize_icon)
                .setContentTitle("Notification Found!")
                .setContentText("Sender was: ${sbn.packageName}")
                .setTicker("test")
                .build()

//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), notification)
//        manager.notify(System.currentTimeMillis().toInt(), notification)
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