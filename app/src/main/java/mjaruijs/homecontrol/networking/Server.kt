package mjaruijs.homecontrol.networking

import mjaruijs.homecontrol.networking.client.MessageClient
import java.net.InetSocketAddress
import java.nio.channels.ServerSocketChannel

class Server(port: Int, private val onRead: (String) -> Unit) : Runnable {

    private val serverChannel: ServerSocketChannel = ServerSocketChannel.open()

    init {
        serverChannel.bind(InetSocketAddress(port))
    }

    override fun run() {
        val client = MessageClient(serverChannel.accept())
        val message = client.read()
        client.close()
        println("ONREAD ${String(message)}")
        onRead(String(message))
    }
}