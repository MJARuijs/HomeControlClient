package mjaruijs.homecontrol.networking

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.client.MessageClient

class MessageSender(private val callback: ConnectionResponse) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg strings: String): String {

        val client = try {
            MessageClient(serverIP, serverPort)
        } catch (e: Exception) {
            return ""
        }

        return try {
            client.writeMessage("PHONE")
            client.writeMessage(strings.joinToString("|", "", "", -1, "", null))

            val response = client.readMessage()
            println("MessageSender: $response")
            response
        } catch (e: Exception) {
            ""
        } finally {
            client.close()
        }
    }

    public override fun onPostExecute(result: String) {
        callback.result(result)
    }

    interface ConnectionResponse {

        fun result(message: String) {}

    }
}