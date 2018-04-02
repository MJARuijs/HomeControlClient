package mjaruijs.servertest.server

import android.os.AsyncTask
import android.util.Log
import mjaruijs.servertest.server.CommandResult.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class CommandConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, CommandResult>() {

    override fun doInBackground(vararg strings: String): CommandResult {
        val client: Socket
        val inputStream: DataInputStream
        val outputStream: DataOutputStream

        try {
            client = Socket("192.168.0.14", 80)
            inputStream = DataInputStream(client.getInputStream())
            outputStream = DataOutputStream(client.getOutputStream())

            Log.i(TAG, "Getting config")
            val printWriter = PrintWriter(outputStream)
            printWriter.println(strings[0])
            printWriter.flush()

            val inputLine = StringBuilder()
            val lines: List<String> = inputStream.reader().readLines()

            lines.forEach { l -> inputLine.append(l)}

            Log.i(TAG, "Line: $inputLine")

            return when (inputLine.toString()) {
                "SUCCESS" -> SUCCESS
                "SOCKET_POWER_OFF" -> MAIN_POWER_OFF
                "PC_STILL_ON" -> PC_STILL_ON
                "SESSION_EXPIRED" -> SESSION_EXPIRED
                else -> FAILED
            }
        } catch (e: IOException) {
            return FAILED
        }

    }

    public override fun onPostExecute(result: CommandResult) {
        response.commandResult(result)
    }

    companion object {
        private const val TAG = "CommandConnection"
    }

}
