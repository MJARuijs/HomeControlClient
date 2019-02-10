package mjaruijs.homecontrol.activities.lampsetup.data.cards

import mjaruijs.homecontrol.activities.lampsetup.data.cards.CardList.Companion.getValue
import java.util.ArrayList
import java.util.Scanner

class Blacklist internal constructor() {

    private val blacklist: MutableList<BlackCard>

    init {
        blacklist = ArrayList()
    }

    internal fun addCard(card: BlackCard) {
        blacklist.add(card)
    }

    operator fun contains(ticker: String): Boolean {
        for (blackCard in blacklist) {
            if (ticker.contains(blackCard.appName)) {
                return true
            }
        }
        return false
    }

    internal fun deleteCard(cardName: String) {
        val appName = cardName.replace("_Del_Btn", "")

        for (blackCard in blacklist) {
            if (blackCard.appName == appName) {
                blacklist.remove(blackCard)
                break
            }
        }
    }

    fun size(): Int {
        return blacklist.size
    }

    operator fun get(i: Int): BlackCard {
        return blacklist[i]
    }

    override fun toString(): String {
        val fileContent = StringBuilder()

        if (blacklist.size > 0) {

            for (blackCard in blacklist) {
                fileContent.append("\n\t\t\t\t<black-card>" + "\n\t\t\t\t<item>")
                        .append(blackCard.item)
                        .append("</item>").append("\n\t\t\t</black-card>")
            }
        }

        return fileContent.toString()
    }

    companion object {

        internal fun readFromXML(sc: Scanner): Blacklist {
            val list = Blacklist()
            var line: String
            var blacklistName = ""

            do {
                line = sc.nextLine()
                if (line.contains("<item>")) {
                    blacklistName = getValue(line)
                } else if (line.contains("</black-card>")) {
                    list.addCard(BlackCard(blacklistName))
                }

            } while (!line.contains("</blacklist>"))

            return list
        }
    }

}
