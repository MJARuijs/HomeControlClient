package mjaruijs.homecontrol.networking.server

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants
import mjaruijs.homecontrol.networking.client.SecureClient
import java.io.IOException

class EndConnection : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void): Void? {

        val client = try {
            SecureClient(Constants.serverIP, Constants.serverPort)
        } catch (e: Exception) {
            return null
        }

        try {
            client.writeMessage("close_connection")
            println("connection Closed")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }

        return null
    }


}
