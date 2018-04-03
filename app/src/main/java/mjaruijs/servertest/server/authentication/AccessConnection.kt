package mjaruijs.servertest.server.authentication

import android.os.AsyncTask
import android.util.Log
import mjaruijs.servertest.networking.SecureClient
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.authentication.ConnectionState.*
import java.io.IOException

class AccessConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, ConnectionState>() {

    override fun doInBackground(vararg strings: String): ConnectionState {

        val client = SecureClient("192.168.0.11", 4444)

        return try {
            Log.i(TAG, "str: ${strings[0]} ${strings[0].length}")
            client.write(strings[0])
            val response = when (client.read()) {
                "ACCESS_GRANTED" -> ACCESS_GRANTED
                else -> ACCESS_DENIED
            }
            Log.i(TAG, "response: $response")
            client.close()
            return response
        } catch (e: IOException) {
            e.printStackTrace()
            NO_CONNECTION
        } finally {
            client.close()
        }

    }

    public override fun onPostExecute(result: ConnectionState) {
        response.resultState(result)
    }

    companion object {
        private val TAG = "AccessConnection"
    }

}
