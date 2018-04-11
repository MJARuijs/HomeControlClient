package mjaruijs.servertest.server

import android.os.AsyncTask
import android.util.Log
import mjaruijs.servertest.networking.networking.SecureClient
import java.io.IOException

class ConfigConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, BooleanArray>() {

    override fun doInBackground(vararg strings: String): BooleanArray {
        val client = SecureClient("192.168.0.11", 4444)

        try {
            client.writeMessage(strings[0])

            val response = client.readMessage()

            val values = response.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val config = BooleanArray(values.size)

            for (value in values) {

                val setting = value.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val key = setting[0]

                setting.forEach { i -> Log.i(TAG, "Setting: $i" ) }

                when (key) {
                    "socket_power" -> config[0] = getBooleanValue(setting[1])
                    "pc_power" -> config[1] = getBooleanValue(setting[1])
                    "light" -> config[2] = getBooleanValue(setting[1])
                }
            }

            return config
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            client.close()
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
