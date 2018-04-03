package mjaruijs.servertest.server.authentication

import android.os.AsyncTask
import mjaruijs.servertest.networking.Client
import mjaruijs.servertest.networking.SecureClient
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.authentication.ConnectionState.ACCESS_DENIED
import mjaruijs.servertest.server.authentication.ConnectionState.NO_CONNECTION
import java.io.IOException

class EnrollFingerConnection(private val response: ConnectionResponse) : AsyncTask<ByteArray, Void, ConnectionState>() {

    override fun doInBackground(vararg params: ByteArray): ConnectionState {

        return try {
            val client = SecureClient(Client("192.168.0.11", 4444))
            client.write("Hello")
            client.close()
            ACCESS_DENIED
        } catch (e: IOException) {
            e.printStackTrace()
            NO_CONNECTION
        }
    }

    public override fun onPostExecute(result: ConnectionState) {
        response.resultState(result)
    }

}