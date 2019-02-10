package mjaruijs.homecontrol.activities.lampsetup.data

import android.graphics.drawable.Drawable

class AppItem(val name: String, val icon: Drawable) : Comparable<AppItem> {

    override fun compareTo(other: AppItem): Int {
        return this.name.compareTo(other.name)
    }
}
