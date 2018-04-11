package mjaruijs.servertest.server.authentication

import android.os.AsyncTask
import android.util.Log
import mjaruijs.servertest.networking.authentication.SignedRequest
import mjaruijs.servertest.networking.networking.Client
import mjaruijs.servertest.networking.networking.SecureClient
import mjaruijs.servertest.server.ConnectionResponse
import java.io.IOException

class EnrollFingerConnection(private val response: ConnectionResponse) : AsyncTask<SignedRequest, Void, SecureClient>() {

    override fun doInBackground(vararg params: SignedRequest): SecureClient {

        return try {
            val client = SecureClient(Client("192.168.0.11", 4444))
            val size = params[0].toString().length
            Log.i("Enroll", "SIZE $size")
            client.writeMessage(params[0].toString())
            client
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e.message)
        }
    }

    public override fun onPostExecute(result: SecureClient) {
        response.passThroughClient(result)
    }

}