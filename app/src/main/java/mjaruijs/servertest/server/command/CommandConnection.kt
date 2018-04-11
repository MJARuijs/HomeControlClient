package mjaruijs.servertest.server.command

import android.os.AsyncTask
import mjaruijs.servertest.networking.networking.SecureClient
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.command.CommandResult.*
import java.io.IOException

class CommandConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, CommandResult>() {

    override fun doInBackground(vararg strings: String): CommandResult {

        val client = SecureClient("192.168.0.11", 4444)

        return try {

            val state = if (strings[1] == "true") "off" else "on"
            client.writeMessage("${strings[0]}_$state")
            val response = client.readMessage()
            when (response) {
                "SUCCESS" -> SUCCESS
                "SOCKET_POWER_OFF" -> MAIN_POWER_OFF
                "PC_STILL_ON" -> PC_STILL_ON
                "SESSION_EXPIRED" -> SESSION_EXPIRED
                else -> FAILED
            }
        } catch (e: IOException) {
            FAILED
        } finally {
            client.close()
        }

    }

    public override fun onPostExecute(result: CommandResult) {
        response.commandResult(result)
    }

    companion object {
        private const val TAG = "CommandConnection"
    }

}
