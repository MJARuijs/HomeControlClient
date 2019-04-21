package mjaruijs.homecontrol.networking.client

import android.util.Base64
import android.util.Base64.NO_WRAP
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

open class EncodedClient(private val channel: SocketChannel) {

    constructor(host: String, port: Int): this(SocketChannel.open()) {
        println("TRYING TO CONNECT")
        channel.connect(InetSocketAddress(host, port))
        println("CONNECTED")
    }

    private val readSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)

    open fun writeMessage(message: String) = write(message.toByteArray())

    fun write(bytes: ByteArray) {
        try {
            val encodedBytes = Base64.encode(bytes, NO_WRAP)
            val bufferSize = encodedBytes.size.toString().toByteArray()
            val encodedSize = Base64.encode(bufferSize, NO_WRAP)
            val buffer = ByteBuffer.allocate(encodedBytes.size + encodedSize.size)
            buffer.put(encodedSize)
            buffer.put(encodedBytes)
            buffer.rewind()
//            println("WRITING ${String(encodedSize)}")
            channel.write(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ClientException("Invalid write!")
        }
    }

    open fun readMessage() = String(read())

    @Throws (ClientException::class)
    fun read(): ByteArray {

        // Read size
        readSizeBuffer.clear()
        val sizeBytesRead = channel.read(readSizeBuffer)

        if (sizeBytesRead == -1) {
            return ByteArray(0)
        }

        readSizeBuffer.rewind()

        // Read data
        val size = String(Base64.decode(readSizeBuffer.array(), NO_WRAP)).toInt()
        if (size > 1000) {
            println("ERROR: too large: $size")
            throw ClientException("Size was too large")
        }

        val data = ByteBuffer.allocate(size)
        val bytesRead = channel.read(data)

        if (bytesRead == -1) {
            close()
            throw ClientException("EncodedClient was closed")
        }

        data.rewind()
        return Base64.decode(data.array(), NO_WRAP)
    }

    private fun close() {
        channel.close()
    }

}