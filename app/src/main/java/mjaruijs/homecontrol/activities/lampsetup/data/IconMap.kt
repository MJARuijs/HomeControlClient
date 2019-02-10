package mjaruijs.homecontrol.activities.lampsetup.data

import android.graphics.drawable.Drawable

import java.util.HashMap

class IconMap {

    private val appMap: HashMap<String, Drawable> = HashMap()

    fun add(appName: String, icon: Drawable) {
        appMap[appName] = icon
    }

    fun getValue(appName: String): Drawable? {
        return appMap[appName]
    }
}
