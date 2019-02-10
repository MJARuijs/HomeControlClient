package mjaruijs.homecontrol.networking.server.authentication

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.authentication.SignedRequest
import mjaruijs.homecontrol.networking.client.SecureClient
import mjaruijs.homecontrol.networking.server.ConnectionResponse
import mjaruijs.homecontrol.networking.server.authentication.AuthenticationResult.*
import java.io.IOException

class EnrollFingerConnection(private val response: ConnectionResponse) : AsyncTask<SignedRequest, Void, AuthenticationResult>() {

    override fun doInBackground(vararg params: SignedRequest): AuthenticationResult {

        val client = try {
            SecureClient(serverIP, serverPort)
        } catch (e: Exception) {
            return NO_CONNECTION
        }

        return try {
            client.writeMessage(params[0].toString())

            val response = client.readMessage()

            if (response == "ENROLLMENT_SUCCESSFUL") ENROLLMENT_SUCCESSFUL else ENROLLMENT_FAILED
        } catch (e: Exception) {
            NO_CONNECTION
        } finally {
            client.close()
        }
    }

    override fun onPostExecute(result: AuthenticationResult) {
        response.authenticationResult(result)
    }

}
