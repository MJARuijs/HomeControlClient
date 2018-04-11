package mjaruijs.networking.networking

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

open class Client(private val channel: SocketChannel) {

    constructor(host: String, port: Int): this(SocketChannel.open()) {
        val address = InetSocketAddress(host, port)
        channel.connect(address)
    }

    private val writeSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)
    private val readSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)

    fun write(bytes: ByteArray) {

        // Prepare size buffer
        writeSizeBuffer.clear()
        writeSizeBuffer.putInt(bytes.size)
        writeSizeBuffer.rewind()

        val buffer = ByteBuffer.wrap(bytes)

        // Write size
        channel.write(writeSizeBuffer)

        // Write data
        channel.write(buffer)
    }

    @Throws (ClientException::class)
    fun read(): ByteBuffer {

        // Read size
        readSizeBuffer.clear()
        val sizeBytesRead = channel.read(readSizeBuffer)

        if (sizeBytesRead == -1) {
            close()
            throw ClientException("Client was closed")
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

        return data
    }

    fun close() {

        // Close channel
        channel.close()
    }

}