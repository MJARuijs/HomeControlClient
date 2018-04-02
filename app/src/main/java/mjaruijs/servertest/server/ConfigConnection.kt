package mjaruijs.servertest.server

import android.os.AsyncTask
import android.util.Log

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class ConfigConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, BooleanArray>() {

    override fun doInBackground(vararg strings: String): BooleanArray {
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

            lines.forEach { l -> inputLine.append(l) }

            Log.i(TAG, "line: $inputLine")

            val values = inputLine.toString().split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val config = BooleanArray(values.size)

            for (value in values) {

                val setting = value.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val key = setting[0]

                setting.forEach { i -> Log.i(TAG, "Setting: $i" ) }

                when (key) {
                    "socket_power" -> config[0] = getBooleanValue(setting[1])
                    "pc_power" -> config[1] = getBooleanValue(setting[1])
                    "light_on" -> config[2] = getBooleanValue(setting[1])
                }
            }

            inputStream.close()
            client.close()
            Log.i(TAG, "Values: " + inputLine.toString())
            return config
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BooleanArray(0)
    }

    public override fun onPostExecute(result: BooleanArray) {
        response.resultBooleanArray(result)
    }

    companion object {

        private val TAG = "ConfigConnection"

        private fun getBooleanValue(input: String): Boolean {
            return input == "true"
        }
    }
}
