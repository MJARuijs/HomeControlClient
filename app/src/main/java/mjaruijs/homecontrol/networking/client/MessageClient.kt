package mjaruijs.homecontrol.networking.client

import java.nio.charset.StandardCharsets.UTF_8

class MessageClient(host: String, port: Int) : Client(host, port) {

    fun writeMessage(message: String) {
        val bytes = message.toByteArray(UTF_8)
        println(message)
        write(bytes)
    }

    fun readMessage(): String {

        return try {
            val buffer = read()
            String(buffer, UTF_8)
        } catch (e: ClientException) {
            "ERROR"
        }
    }

}
