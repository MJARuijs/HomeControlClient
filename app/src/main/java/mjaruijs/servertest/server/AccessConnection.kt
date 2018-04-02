package mjaruijs.servertest.server

import android.os.AsyncTask
import mjaruijs.servertest.server.ConnectionState.NO_CONNECTION
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets.UTF_8

class AccessConnection(response: ConnectionResponse) : AsyncTask<String, Void, ConnectionState>() {

    private val response: ConnectionResponse? = response

    override fun doInBackground(vararg strings: String): ConnectionState {
        val address = InetSocketAddress("192.168.0.11", 4444)
        val client: SocketChannel

        return try {
            client = SocketChannel.open(address)

            val bytes = strings[0].toByteArray(UTF_8)
            val buffer = ByteBuffer.wrap(bytes)

            val sizeBuffer = ByteBuffer.allocate(Integer.BYTES)

            sizeBuffer.putInt(bytes.size)
            sizeBuffer.rewind()

            client.write(sizeBuffer)

            client.write(buffer)
            client.close()

            ConnectionState.ACCESS_DENIED
        } catch (e: IOException) {
            e.printStackTrace()
            NO_CONNECTION
        }

    }

    public override fun onPostExecute(result: ConnectionState) {
        response!!.resultState(result)
    }

    companion object {

        private val TAG = "AccessConnection"
    }

}
