package mjaruijs.homecontrol.activities.lampsetup.appcardlist

import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import mjaruijs.homecontrol.colorpicker.ColorPickerView
import mjaruijs.homecontrol.data.InstalledAppsCache
import mjaruijs.homecontrol.activities.dialogs.DynamicAlertDialog
import mjaruijs.homecontrol.activities.lampsetup.applist.AppListItem
import mjaruijs.homecontrol.activities.lampsetup.blacklist.BlackListAdapter
import mjaruijs.homecontrol.activities.lampsetup.blacklist.BlackListItem
import mjaruijs.homecontrol.activities.lampsetup.subcardlist.SubCardAdapter
import mjaruijs.homecontrol.activities.lampsetup.subcardlist.SubCardItem

class AppCardItem(val dynamicDialog: DynamicAlertDialog,
                  val name: String,
                  val icon: Drawable,
                  var color: Int = -1,
                  colorPickerView: ColorPickerView,
                  val subCards: ArrayList<SubCardItem> = ArrayList(),
                  val blackList: ArrayList<BlackListItem> = ArrayList(),
                  var cardView: CardView? = null) {

    constructor(dynamicDialog: DynamicAlertDialog, colorPickerView: ColorPickerView, appListItem: AppListItem) : this(dynamicDialog, appListItem.name, appListItem.icon, colorPickerView = colorPickerView)

    val subListView = RecyclerView(dynamicDialog.context)
    val subListAdapter = SubCardAdapter(dynamicDialog, colorPickerView, subCards)

    val blackListView = RecyclerView(dynamicDialog.context)
    val blackListAdapter = BlackListAdapter(blackList)

    init {
        subListView.layoutManager = LinearLayoutManager(dynamicDialog.context)
        subListView.adapter = subListAdapter

        blackListView.layoutManager = LinearLayoutManager(dynamicDialog.context)
        blackListView.adapter = blackListAdapter
    }

    fun getInfo(): AppCardInfo {
        return AppCardInfo(name, color, subCards, blackList)
    }

    companion object {
        fun parse(dynamicDialog: DynamicAlertDialog, colorPickerView: ColorPickerView, string: String): AppCardItem {
            val values = string.split('|')
            val appInfo = values[0].split(';')

            val nameStartIndex = appInfo[0].indexOf('=') + 1
            val name = appInfo[0].substring(nameStartIndex)

            val iconStartIndex = appInfo[1].indexOf('=') + 1
            val iconString = appInfo[1].substring(iconStartIndex)
            val icon = InstalledAppsCache.getIcon(iconString.toInt())

            val colorStartIndex = appInfo[2].indexOf('=') + 1
            val color = appInfo[2].substring(colorStartIndex).toInt()

            val subListStartIndex = values[1].indexOf('[') + 1
            val subListEndIndex = values[1].lastIndexOf(']')
            val subList = SubCardAdapter.parse(values[1].substring(subListStartIndex, subListEndIndex))

            val blackListStartIndex = values[2].indexOf('[') + 1
            val blackListEndIndex = values[2].lastIndexOf(']')
            val blackList = BlackListAdapter.parse(values[2].substring(blackListStartIndex, blackListEndIndex))

            return AppCardItem(dynamicDialog, name, icon.icon, color, colorPickerView, subList, blackList)
        }

    }

    override fun toString(): String {
        val iconId = InstalledAppsCache.getIcon(icon).id
        return "name=$name;icon=$iconId;color=$color|subList=[$subListAdapter]|blackList=[$blackListAdapter]"
    }
}