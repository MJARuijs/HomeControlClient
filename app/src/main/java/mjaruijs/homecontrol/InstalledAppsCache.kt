package mjaruijs.homecontrol

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import mjaruijs.homecontrol.activities.lampsetup.PackageInfo
import java.util.concurrent.atomic.AtomicBoolean

object InstalledAppsCache {

    private val apps = ArrayList<PackageInfo>()
    private val locked = AtomicBoolean(false)

    fun get(context: Context): ArrayList<PackageInfo> {
        while (locked.get()) {}

        locked.set(true)
        if (apps.isEmpty()) {
            val flags = PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
            val packageManager = context.packageManager

            val installedApps = packageManager.getInstalledApplications(flags)

            for (appInfo in installedApps) {
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    val appName = appInfo.loadLabel(packageManager).toString()
                    val icon = appInfo.loadIcon(packageManager)
                    apps += PackageInfo(appName, icon)
                }
            }

        }

        apps.sortBy { app -> app.name }

        locked.set(false)
        return apps
    }


}