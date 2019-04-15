package mjaruijs.homecontrol.services

import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

class ExceptionHandler : Thread.UncaughtExceptionHandler {

    private val handler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)

        e.printStackTrace(printWriter)

        val stackTrace = stringWriter.toString()
        printWriter.close()

        writeToFile(stackTrace)

        handler.uncaughtException(t, e)
    }

    private fun writeToFile(stackTrace: String) {
        try {
            val path = File(Environment.getExternalStorageDirectory(), "Crash_Reports")

            if (!path.exists()) {
                path.mkdir()
            }

            val date = Date()

            val fileName = SimpleDateFormat.getDateInstance(2).format(date) + "_" +  SimpleDateFormat.getTimeInstance().format(date) + ".txt"
            val file = File(path, fileName)

            val fileWriter = FileWriter(file)
            fileWriter.append(stackTrace)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            println("FAILED TO WRITE ERROR MESSAGE")
        }
    }

}