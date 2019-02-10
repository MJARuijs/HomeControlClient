package mjaruijs.homecontrol.activities.lampsetup.data.cards

import java.util.ArrayList

abstract class CardList internal constructor() {

    init {
        list = ArrayList()
    }

    internal fun addCard(card: Card) {
        list.add(card)
    }

    operator fun contains(appName: String): Boolean {

        for (card in list) {

            if (card.appName == appName) {
                return true
            }

        }
        return false
    }

    open fun deleteCard(name: String) {

        val appName = name.replace("_Del_Btn", "")

        for (i in list.indices) {

            if (list[i].appName == appName) {
                list.removeAt(i)
                break
            }

        }
    }

    open fun getByName(appName: String): Card? {
        for (card in list) {
            if (card.appName == appName) {
                return card
            }
        }
        return null
    }

    open fun multipleSelected(): Boolean {
        var counter = 0
        for (card in list) {
            if (card.selected) {
                counter++
            }
            if (counter > 1) {
                return true
            }
        }
        return false
    }

    open operator fun get(i: Int): Card {
        return list[i]
    }

    fun size(): Int {
        return list.size
    }

    open fun clear() {
        list.clear()
    }

    override fun toString(): String {
        val string = StringBuilder()
        for (card in list) {
            string.append(card.appName).append(" ").append(card.appIcon).append(" ").append(" \n")
        }
        return string.toString()
    }

    companion object {

        lateinit var list: MutableList<Card>

        internal fun getValue(line: String): String {
            val begin = line.indexOf(">")
            val end = line.indexOf("<", begin)
            val res = StringBuilder()
            for (i in begin + 1 until end) {
                res.append(line[i])
            }
            return res.toString()
        }
    }
}
