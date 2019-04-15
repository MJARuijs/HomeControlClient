package mjaruijs.homecontrol.networking.client

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets.UTF_8

open class Client(private val channel: SocketChannel) {

    private val writeSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)
    private val readSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)

    fun write(bytes: ByteArray) {
        val buffer = ByteBuffer.wrap(Base64.encode(bytes, Base64.NO_WRAP))

        val bufferSize = buffer.array().size.toString()
        val size = Base64.encode(bufferSize.toByteArray(UTF_8), Base64.NO_WRAP)

        // Prepare size buffer
        writeSizeBuffer.clear()
        writeSizeBuffer.put(size)
        writeSizeBuffer.rewind()

        // Write size
        channel.write(writeSizeBuffer)

        buffer.rewind()

        // Write data
        channel.write(buffer)
    }

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
        val size = String(Base64.decode(readSizeBuffer.array(), Base64.NO_WRAP), UTF_8).toInt()
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
        return Base64.decode(data.array(), Base64.NO_WRAP)
    }

    private fun close() {
        channel.close()
    }

}