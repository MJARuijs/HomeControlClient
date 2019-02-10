package mjaruijs.homecontrol.networking.server.command

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.client.SecureClient
import mjaruijs.homecontrol.networking.server.ConnectionResponse
import mjaruijs.homecontrol.networking.server.command.CommandResult.*
import java.io.IOException

class CommandConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, Any>() {

    override fun doInBackground(vararg strings: String): Any {

        val client = try {
            SecureClient(serverIP, serverPort)
        } catch (e: Exception) {
            return NO_CONNECTION
        }

        return try {
            if (strings[0] == "confirm_pc_shutdown" || strings[0] == "confirm_socket_off") {
                client.writeMessage(strings[0])
            } else {
                val state = if (strings[1] == "true") "off" else "on"
                client.writeMessage("${strings[0]}_$state")
            }

            val response = client.readMessage()
            when (response) {
                "SESSION_EXPIRED" -> SESSION_EXPIRED
                "PC_STILL_ON" -> PC_STILL_ON

                else -> {
                    if (response.startsWith("configuration: ")) {

                        val values = response
                                .removePrefix("configuration: ")
                                .split(", ".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()

                        val config = BooleanArray(4)

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

                        config
                    } else {
                        FAILED
                    }
                }
            }
        } catch (e: Exception) {
            NO_CONNECTION
        } finally {
            client.close()
        }

    }

    public override fun onPostExecute(result: Any) {
        response.config(result)
    }

    companion object {
        private fun getBooleanValue(input: String): Boolean {
            return input == "true"
        }
    }

}
