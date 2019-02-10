package mjaruijs.homecontrol.networking.server.authentication

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.client.SecureClient
import mjaruijs.homecontrol.networking.server.ConnectionResponse
import mjaruijs.homecontrol.networking.server.authentication.AuthenticationResult.*

class AuthenticationConnection(private val response: ConnectionResponse) : AsyncTask<String, Void, AuthenticationResult>() {

    override fun doInBackground(vararg strings: String): AuthenticationResult {

        val client = try {
            SecureClient(serverIP, serverPort)
        } catch (e: Exception) {
            return NO_CONNECTION
        }

        return try {
            client.writeMessage(strings[0])
            val response = client.readMessage()

            when (response) {
                "ACCESS_GRANTED" -> ACCESS_GRANTED
                else -> ACCESS_DENIED
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NO_CONNECTION
        } finally {
            client.close()
        }

    }

    public override fun onPostExecute(result: AuthenticationResult) {
        response.authenticationResult(result)
    }

    companion object {
        private const val TAG = "AccessConnection"
    }

}
