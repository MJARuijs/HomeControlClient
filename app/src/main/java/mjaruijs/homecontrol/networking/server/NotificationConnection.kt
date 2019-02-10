package mjaruijs.homecontrol.networking.server

import android.os.AsyncTask
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.colorpicker.Color
import mjaruijs.homecontrol.networking.client.SecureClient

class NotificationConnection : AsyncTask<Color, Void, Void?>() {

    override fun doInBackground(vararg colors: Color): Void? {
        val client = try {
            SecureClient(serverIP, serverPort)
        } catch (e: Exception) {
            return null
        }

        try {
            val colorString = parseColor(colors[0])
            client.writeMessage(colorString)
        } catch (e: Exception) {
            System.err.println("Failed to connect to server..")
        } finally {
            client.close()
        }

        return null
    }

    private fun parseColor(color: Color): String = "NotificationColor: r=${color.r}, g=${color.g}, b=${color.b}"

}