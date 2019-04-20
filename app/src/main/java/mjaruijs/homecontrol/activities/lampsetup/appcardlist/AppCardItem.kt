package mjaruijs.homecontrol.activities.lampsetup.appcardlist

import android.graphics.drawable.Drawable
import mjaruijs.homecontrol.activities.lampsetup.applist.AppListItem

class AppCardItem(val name: String, val icon: Drawable, val color: Int, var xPosition: Float = 0.0f, var isMoving: Boolean = false) {

    constructor(appListItem: AppListItem) : this(appListItem.name, appListItem.icon, -1)

}