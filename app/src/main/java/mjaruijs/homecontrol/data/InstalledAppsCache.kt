package mjaruijs.homecontrol.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import mjaruijs.homecontrol.R
import java.util.concurrent.atomic.AtomicBoolean

object InstalledAppsCache {

    private val apps = ArrayList<PackageInfo>()
    private val icons = HashSet<Icon>()
    private val locked = AtomicBoolean(false)

    fun get(context: Context): ArrayList<PackageInfo> {
        while (locked.get()) {}

        locked.set(true)
        if (apps.isEmpty()) {
            val flags = PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
            val packageManager = context.packageManager

            val installedApps = packageManager.getInstalledApplications(flags)

            icons += Icon(0, context.getDrawable(R.drawable.ic_launcher_background))

            var i = 1
            for (appInfo in installedApps) {
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    val appName = appInfo.loadLabel(packageManager).toString()
                    val icon = appInfo.loadIcon(packageManager)
                    apps += PackageInfo(appName, icon)
                    icons += Icon(i++, icon)
                }
            }

        }

        apps.sortBy { app -> app.name }

        locked.set(false)
        return apps
    }

    fun getIcon(id: Int): Icon {
        return icons.find { icon -> icon.id == id } ?: icons.first()
    }

    fun getIcon(drawable: Drawable): Icon {
        return icons.find { icon -> icon.icon == drawable } ?: icons.first()
    }

}