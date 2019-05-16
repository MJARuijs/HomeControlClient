package mjaruijs.homecontrol.data

import android.content.Context
import mjaruijs.homecontrol.colorpicker.ColorPickerView
import mjaruijs.homecontrol.activities.dialogs.DynamicAlertDialog
import mjaruijs.homecontrol.activities.lampsetup.appcardlist.AppCardInfo
import mjaruijs.homecontrol.activities.lampsetup.appcardlist.AppCardItem
import java.io.File
import java.lang.Exception

object AppCardData {

    private var appCards = ArrayList<AppCardItem>()
    private var appCardInfos = ArrayList<AppCardInfo>()
    private const val FILE_NAME = "app_cards.txt"

    fun getAppCards(context: Context, colorPickerView: ColorPickerView, dynamicDialog: DynamicAlertDialog): ArrayList<AppCardItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }

        appCards.clear()
        appCardInfos.clear()

        val lines = file.readLines()
        lines.forEach { line ->
            try {
                val appCard = AppCardItem.parse(dynamicDialog, colorPickerView, line)
                appCards.add(appCard)
                appCardInfos.add(appCard.getInfo())
            } catch (e: Exception) {

            }

        }
        return appCards
    }

    fun setCards(cards: ArrayList<AppCardItem>) {
        appCards = cards
    }

    fun getCardsInfo(context: Context): ArrayList<AppCardInfo> {
        if (appCardInfos.isNotEmpty()) {
            return appCardInfos
        }

        val file = File(context.filesDir, FILE_NAME)

        val lines = file.readLines()
        lines.forEach { line ->
            try {
                val appCardInfo = AppCardInfo.parse(line)
                appCardInfos.add(appCardInfo)
            } catch (e: Exception) {

            }

        }
        return appCardInfos
    }

    fun getCards() = appCards

    fun write(context: Context, cards: ArrayList<AppCardItem>) {
        setCards(cards)
        write(context)
    }

    private fun write(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        var content = ""
        for (card in appCards) {
            content += "$card\n"
        }
        file.writeText(content)
    }

}