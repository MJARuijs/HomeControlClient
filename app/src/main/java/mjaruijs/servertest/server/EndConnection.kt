package mjaruijs.servertest.server

import android.os.AsyncTask
import mjaruijs.servertest.networking.networking.SecureClient
import mjaruijs.servertest.server.authentication.ConnectionState
import mjaruijs.servertest.server.authentication.ConnectionState.CONNECTION_CLOSED
import mjaruijs.servertest.server.authentication.ConnectionState.NO_CONNECTION
import java.io.IOException

class EndConnection(private val response: ConnectionResponse) : AsyncTask<Void, Void, ConnectionState>() {

    override fun doInBackground(vararg params: Void?): ConnectionState {
        val client = SecureClient("192.168.0.11", 4444)

        return try {
            client.writeMessage("close_connection")

            val response = client.readMessage()

            if (response == "CONNECTION_CLOSED") CONNECTION_CLOSED else NO_CONNECTION
        } catch (e: IOException) {
            e.printStackTrace()
            NO_CONNECTION
        } finally {
            client.close()
        }
    }

    override fun onPostExecute(result: ConnectionState) {
        response.resultState(result)
    }
}