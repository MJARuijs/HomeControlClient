package mjaruijs.servertest.server

import android.os.AsyncTask
import android.util.Log
import mjaruijs.servertest.server.ConnectionState.CONNECTION_CLOSED
import mjaruijs.servertest.server.ConnectionState.NO_CONNECTION
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class EndConnection(private val response: ConnectionResponse) : AsyncTask<Void, Void, ConnectionState>() {

    override fun doInBackground(vararg params: Void?): ConnectionState {
        val client: Socket
        val inputStream: DataInputStream
        val outputStream: DataOutputStream

        try {
            client = Socket("192.168.0.14", 80)
            inputStream = DataInputStream(client.getInputStream())
            outputStream = DataOutputStream(client.getOutputStream())

            val printWriter = PrintWriter(outputStream)
            printWriter.println("close_connection")
            printWriter.flush()

            val inputLine = StringBuilder()
            val lines = inputStream.reader().readLines()

            lines.forEach { l -> inputLine.append(l) }

            Log.i("EndConnection", "result: $inputLine")

            inputStream.close()
            client.close()
            return if (inputLine.toString() == "CONNECTION_CLOSED") CONNECTION_CLOSED else NO_CONNECTION
        } catch (e: IOException) {
            e.printStackTrace()
            return NO_CONNECTION
        }
    }

    override fun onPostExecute(result: ConnectionState) {
        response.resultState(result)
    }
}