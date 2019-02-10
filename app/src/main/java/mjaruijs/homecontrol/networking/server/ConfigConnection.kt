package mjaruijs.homecontrol.networking.server

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.client.SecureClient
import java.io.IOException

class ConfigConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, BooleanArray>() {

    override fun doInBackground(vararg strings: String): BooleanArray {
        val client = try {
            SecureClient(serverIP, serverPort)
        } catch (e: Exception) {
            return BooleanArray(0)
        }

        try {
            client.writeMessage(strings[0])

            val response = client.readMessage().removePrefix("configuration: ")
            println("RESPONSE: $response")
            val values = response.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val config = BooleanArray(values.size)

            for (value in values) {

                val setting = value.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val key = setting[0]

                when (key) {
                    "socket_power" -> config[0] = getBooleanValue(setting[1])
                    "pc_power" -> config[1] = getBooleanValue(setting[1])
                    "light" -> config[2] = getBooleanValue(setting[1])
                    "use_motion_sensor" -> config[3] = getBooleanValue(setting[1])
                }
            }

            return config
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }

        return BooleanArray(0)
    }

    public override fun onPostExecute(result: BooleanArray) {
        response.resultBooleanArray(result)
    }


    private fun getBooleanValue(input: String): Boolean {
        return input == "true"
    }
}
