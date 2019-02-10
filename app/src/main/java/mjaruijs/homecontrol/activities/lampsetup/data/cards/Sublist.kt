package mjaruijs.homecontrol.activities.lampsetup.data.cards

import mjaruijs.homecontrol.activities.lampsetup.data.cards.CardList.Companion.getValue
import mjaruijs.homecontrol.colorpicker.ColorList

import java.util.ArrayList
import java.util.Scanner

class Sublist internal constructor() {

    private val sublist: MutableList<SubCard>

    init {
        sublist = ArrayList()
    }

    internal fun addCard(subCard: SubCard) {
        sublist.add(subCard)
    }

//    fun getSubColor(ticker: String): Int {
//        for (subCard in sublist) {
//            if (ticker.contains(subCard.item)) {
//                return subCard.color
//            }
//        }
//        return 0
//    }

    internal fun deleteCard(cardName: String) {
        val appName = cardName.replace("_Del_Btn", "")
        for (subCard in sublist) {
            if (subCard.item == appName) {
                sublist.remove(subCard)
                break
            }
        }
    }

    fun size(): Int {
        return sublist.size
    }

    operator fun get(name: String): SubCard? {
        for (subCard in sublist) {
            if (subCard.item == name) {
                return subCard
            }
        }
        return null
    }

    operator fun get(i: Int): SubCard {
        return sublist[i]
    }

    override fun toString(): String {
        val fileContent = StringBuilder()

        if (sublist.size > 0) {

            for (subCard in sublist) {
                fileContent.append("\n\t\t\t\t<sub-card>")
                        .append("\n\t\t\t\t\t<item>").append(subCard.item).append("</item>")
                        .append("\n\t\t\t\t\t<color>").append(subCard.color.intValue).append("</color>")
                        .append("\n\t\t\t\t</sub-card>")
            }
        }

        return fileContent.toString()
    }

    companion object {

        internal fun readFromXML(sc: Scanner): Sublist {
            val list = Sublist()
            var line: String
            var color = 0
            var subListName = ""

            do {
                if (!sc.hasNext()) {
                    return list
                }

                line = sc.nextLine()

                when {
                    line.contains("item") -> subListName = getValue(line)
                    line.contains("color") -> color = Integer.parseInt(getValue(line))
                    line.contains("</sub-card>") -> list.addCard(SubCard(subListName, ColorList.getByIntValue(color)))
                }
            } while (!line.contains("</sub-getCards>"))

            return list
        }
    }

}
