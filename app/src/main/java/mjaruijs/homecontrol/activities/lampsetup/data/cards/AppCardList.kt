package mjaruijs.homecontrol.activities.lampsetup.data.cards

import android.graphics.drawable.Drawable

import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Scanner
import mjaruijs.homecontrol.colorpicker.Color
import mjaruijs.homecontrol.colorpicker.ColorList

class AppCardList : CardList() {

    val selectedCards: List<AppCard>
        get() {
            val selectedCards = ArrayList<AppCard>()

            for (card in cards) {
                if (card.selected) {
                    selectedCards.add(card)
                }
            }
            return selectedCards
        }

    fun deselectCards() {
        for (card in cards) {
            card.selected = false
        }
    }

    fun addCard(card: AppCard) {
        super.addCard(card)
        cards.add(card)
    }

    override fun deleteCard(name: String) {
        super.deleteCard(name)
        val appName = name.replace("_Del_Btn", "")

        for (i in cards.indices) {

            if (cards[i].appName == appName) {
                cards.removeAt(i)
                break
            }

        }
    }

    override fun multipleSelected(): Boolean {
        var counter = 0

        for (card in cards) {
            if (card.selected) {
                counter++
            }

            if (counter > 1) {
                return true
            }

        }
        return false
    }

    override fun get(i: Int): AppCard {
        return cards[i]
    }

    override fun getByName(appName: String): AppCard? {
        super.getByName(appName)
        for (card in cards) {

            if (card.appName == appName) {
                return card
            }

        }
        return null
    }

    override fun clear() {
        super.clear()
        cards.clear()
    }

    override fun toString(): String {
        val fileContent = StringBuilder("\n<app-list>")

        if (cards.size > 0) {

            for (card in cards) {

                fileContent
                        .append("\n\t\t<app-card>" + "\n\t\t\t<name>").append(card.appName).append("</name>")
                        .append("\n\t\t\t<color>").append(card.color.intValue).append("</color>")
                        .append("\n\t\t\t<sub-getCards>").append(card.sublist.toString()).append("\n\t\t\t</sub-getCards>")
                        .append("\n\t\t\t<blacklist>").append(card.blacklist.toString()).append("\n\t\t\t</blacklist>")
                        .append("\n\t\t</app-card>")

            }
        }

        fileContent.append("\n</app-list>")
        return fileContent.toString()
    }

    companion object {

        private var file: File? = null
        var cards = ArrayList<AppCard>()

        fun initialize(appFile: File) {
            file = appFile
        }

        fun readFromXML(iconMap: HashMap<String, Drawable>): AppCardList {
            val list = AppCardList()
            var line: String
            var appName = ""
            var icon: Drawable? = null
            var color = Color(-1, 0f, 0f, 0f)
            var blacklist = Blacklist()
            var sublist = Sublist()

            try {
                val sc = Scanner(file)

                if (sc.hasNextLine()) {
                    do {
                        line = sc.nextLine()
                        when {
                            line.contains("<name>") -> {
                                appName = getValue(line)
                                icon = iconMap.getValue(appName)
                            }
                            line.contains("<color>") -> color = ColorList.getByIntValue(Integer.parseInt(getValue(line)))
                            line.contains("<blacklist>") -> blacklist = Blacklist.readFromXML(sc)
                            line.contains("<sub-getCards>") -> sublist = Sublist.readFromXML(sc)
                            line.contains("</app-card>") -> list.addCard(AppCard(appName, icon!!, color, blacklist, sublist))
                        }
                    } while (!line.contains("</app-list>") && sc.hasNextLine())
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return list
        }
    }
}
