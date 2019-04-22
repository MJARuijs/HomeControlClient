package mjaruijs.homecontrol.data

import android.content.Context
import mjaruijs.homecontrol.activities.dialogs.DynamicAlertDialog
import mjaruijs.homecontrol.activities.lampsetup.appcardlist.AppCardItem
import java.io.File
import java.lang.Exception

object AppCardData {

    private var appCards = ArrayList<AppCardItem>()
    private const val FILE_NAME = "app_cards.txt"

    fun getAppCards(context: Context, dynamicDialog: DynamicAlertDialog): ArrayList<AppCardItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }

        appCards.clear()

        val lines = file.readLines()
        lines.forEach { line ->
            try {
                appCards.add(AppCardItem.parse(dynamicDialog, line))
            } catch (e: Exception) {

            }

        }
        return appCards
    }

    private fun setCards(cards: ArrayList<AppCardItem>) {
        appCards = cards
    }

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