package mjaruijs.homecontrol.networking.client

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class MessageClient(private val channel: SocketChannel) {

    constructor(host: String, port: Int): this(SocketChannel.open()) {
        val address = InetSocketAddress(host, port)
        channel.connect(address)
    }

    private val readSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)

    fun writeMessage(message: String) = write(message.toByteArray())

    private fun write(bytes: ByteArray) {
        val buffer = ByteBuffer.allocate(bytes.size + 4)
        buffer.putInt(bytes.size)
        buffer.put(bytes)
        buffer.rewind()

        channel.write(buffer)
    }

    fun readMessage() = String(read())

    @Throws (ClientException::class)
    fun read(): ByteArray {

        // Read size
        readSizeBuffer.clear()
        val sizeBytesRead = channel.read(readSizeBuffer)

        if (sizeBytesRead == -1) {
            throw ClientException("Size was too large")
        }

        readSizeBuffer.rewind()

        // Read data
        val size = readSizeBuffer.int

        if (size > 1000) {
            println("ERROR: too large: $size")
            throw ClientException("Size was too large")
        }

        val data = ByteBuffer.allocate(size)
        val bytesRead = channel.read(data)

        if (bytesRead == -1) {
            close()
            throw ClientException("Client was closed")
        }

        data.rewind()
        return data.array()
    }

    fun close() {
        channel.close()
    }

}
