package mjaruijs.homecontrol.services

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import mjaruijs.homecontrol.colorpicker.ColorList
import mjaruijs.homecontrol.data.AppCardData
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
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())

    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        settings.apply(true)

        if (sbn.packageName == "mjaruijs.homecontrol") return
        println(sbn.tag)
        println(sbn.key)
        println(sbn.groupKey)
        println(sbn.id)
        val appName = getAppName(sbn.packageName) ?: throw IllegalArgumentException()
        val tickerText = sbn.notification.tickerText

        if (appName == "System UI") return

        if (appName.contains("Google")) return

        if (settings.enableNotificationLighting) {
            val card = AppCardData.getCardsInfo(applicationContext).find { card -> card.name == appName } ?: return
            println("TICKER TEXT: $tickerText")
            if (card.blackList.any { blackListItem -> tickerText.contains(blackListItem.name) }) {
                return
            }

            val strip2Color = card.subCards.find { subCard -> tickerText.contains(subCard.name) } ?.color ?: card.color

            if (NetworkManager.ready()) {
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByIntValue(card.color)}\\strip=2;${ColorList.getByIntValue(strip2Color)}")
            } else {
                NetworkManager.run(applicationContext)
                while (!NetworkManager.ready()) {}

                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByIntValue(card.color)}\\strip=2;${ColorList.getByIntValue(strip2Color)}")

                Thread.sleep(500)
                NetworkManager.stopClient()
            }

            newNotificationPosted = true
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        settings.apply(true)

        val appName = getAppName(sbn.packageName) ?: throw IllegalArgumentException()

        if (newNotificationPosted && settings.enableNotificationLighting) {
            if (AppCardData.getCardsInfo(applicationContext).none { card -> card.name == appName }) {
                return
            }

            if (NetworkManager.ready()) {
                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByID(ColorList.selection1)}\\strip=2;${ColorList.getByID(ColorList.selection2)}")
            } else {
                NetworkManager.run(applicationContext)
                while (!NetworkManager.ready()) {}

                NetworkManager.addMessage("", "StudyRoom|led_strip|strip=1;${ColorList.getByID(ColorList.selection1)}\\strip=2;${ColorList.getByID(ColorList.selection2)}")
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