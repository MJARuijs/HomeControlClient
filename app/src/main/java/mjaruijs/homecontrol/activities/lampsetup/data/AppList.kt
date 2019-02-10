package mjaruijs.homecontrol.activities.lampsetup.data

import java.util.ArrayList
import java.util.Comparator

class AppList {

    private val apps: MutableList<AppItem> = ArrayList()

    fun add(app: AppItem) {
        apps.add(app)
    }

    operator fun get(position: Int): AppItem {
        return apps[position]
    }

    fun size(): Int {
        return apps.size
    }

    fun sort() {
        apps.sortWith(Comparator { o1, o2 -> o1.name.toLowerCase().compareTo(o2.name.toLowerCase()) })
    }
}
