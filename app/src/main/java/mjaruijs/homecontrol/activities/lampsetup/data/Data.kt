package mjaruijs.homecontrol.activities.lampsetup.data

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

import mjaruijs.homecontrol.activities.lampsetup.data.cards.AppCardList

object Data {

    private var appCardList: AppCardList? = null
    private var file: File? = null
    private var initialized = false

    fun initialize(appFile: File, iconMap: IconMap) {
        val fileName = "app_array.xml"
        file = File(appFile, fileName)
        AppCardList.initialize(file!!)
        appCardList = AppCardList.readFromXML(iconMap)
        initialized = true
    }

    fun getCards(file: File, iconMap: IconMap): AppCardList? {
        if (!initialized) {
            initialize(file, iconMap)
        }
        return appCardList
    }

    fun getCards(): AppCardList? {
        return appCardList
    }

    fun writeToFile() {

        try {
            val fileWriter = FileWriter(file!!)
            val printWriter = PrintWriter(fileWriter)
            var fileContent = "<resources>"
            fileContent += appCardList!!.toString()

            fileContent += "\n</resources>"

            printWriter.write(fileContent)
            printWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
