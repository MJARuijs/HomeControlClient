package mjaruijs.homecontrol.activities.lampsetup.appcardlist

import mjaruijs.homecontrol.activities.lampsetup.blacklist.BlackListAdapter
import mjaruijs.homecontrol.activities.lampsetup.blacklist.BlackListItem
import mjaruijs.homecontrol.activities.lampsetup.subcardlist.SubCardAdapter
import mjaruijs.homecontrol.activities.lampsetup.subcardlist.SubCardItem

data class AppCardInfo(val name: String, val color: Int, val subCards: ArrayList<SubCardItem>, val blackList: ArrayList<BlackListItem>) {
    companion object {

        fun parse(string: String): AppCardInfo {
            val values = string.split('|')
            val appInfo = values[0].split(';')

            val nameStartIndex = appInfo[0].indexOf('=') + 1
            val name = appInfo[0].substring(nameStartIndex)

            val colorStartIndex = appInfo[2].indexOf('=') + 1
            val color = appInfo[2].substring(colorStartIndex).toInt()

            val subListStartIndex = values[1].indexOf('[') + 1
            val subListEndIndex = values[1].lastIndexOf(']')
            val subList = SubCardAdapter.parse(values[1].substring(subListStartIndex, subListEndIndex))

            val blackListStartIndex = values[2].indexOf('[') + 1
            val blackListEndIndex = values[2].lastIndexOf(']')
            val blackList = BlackListAdapter.parse(values[2].substring(blackListStartIndex, blackListEndIndex))
            return AppCardInfo(name, color, subList, blackList)
        }
    }
}