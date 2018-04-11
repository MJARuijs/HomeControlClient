package mjaruijs.networking.networking

import java.nio.charset.StandardCharsets.UTF_8

class MessageClient(host: String, port: Int) : Client(host, port) {

    fun writeMessage(message: String) {
        val bytes = message.toByteArray(UTF_8)
        write(bytes)
    }

    fun readMessage(): String {

        return try {
            val buffer = read()
            val bytes = buffer.array()
            String(bytes, UTF_8)
        } catch (e: ClientException) {
            "ERROR"
        }


    }

}
