package mjaruijs.homecontrol.services

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import mjaruijs.homecontrol.colorpicker.ColorList
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.settings.Settings

class NotificationListener : NotificationListenerService() {

    private lateinit var apps: List<PackageInfo>
    private lateinit var settings: Settings
    private var newNotificationPosted = false

    override fun onCreate() {
        super.onCreate()
        apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES)
        settings = Settings(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        settings.apply(true)

        if (sbn.packageName == "mjaruijs.homecontrol") return

        val appName = getAppName(sbn.packageName) ?: throw IllegalArgumentException()

        if (appName == "System UI") return

        if (appName.contains("Google")) return

        if (settings.enableNotificationLighting && NetworkManager.isConnectedToHomeNetwork()) {
            if (NetworkManager.ready()) {
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByID(9)}")
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=2;${ColorList.getByID(9)}")
            } else {
                NetworkManager.startClient(applicationContext)
                while (!NetworkManager.ready()) {}

                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByID(9)}")
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=2;${ColorList.getByID(9)}")

                Thread.sleep(500)
                NetworkManager.stopClient()
            }

            newNotificationPosted = true
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        settings.apply(true)

        if (newNotificationPosted && settings.enableNotificationLighting && NetworkManager.isConnectedToHomeNetwork()) {
            if (NetworkManager.ready()) {
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByID(ColorList.selection1)}")
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=2;${ColorList.getByID(ColorList.selection2)}")
            } else {
                NetworkManager.startClient(applicationContext)
                while (!NetworkManager.ready()) {}

                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByID(ColorList.selection1)}")
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=2;${ColorList.getByID(ColorList.selection2)}")
                Thread.sleep(500)
                NetworkManager.stopClient()
            }

            newNotificationPosted = false
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