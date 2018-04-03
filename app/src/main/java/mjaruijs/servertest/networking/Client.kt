package mjaruijs.servertest.networking

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import kotlin.text.Charsets.UTF_8

class Client(private val channel : SocketChannel) {

    constructor(host : String, port : Int) : this(SocketChannel.open()) {
        val address = InetSocketAddress(host, port)
        channel.connect(address)
    }

    private val writeSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)
    private val readSizeBuffer = ByteBuffer.allocateDirect(Integer.BYTES)

    fun write(message: String) {
        write(message.toByteArray(UTF_8))
    }

    fun write(bytes: ByteArray) {

        writeSizeBuffer.clear()
        writeSizeBuffer.putInt(bytes.size)
        writeSizeBuffer.rewind()

        val buffer = ByteBuffer.wrap(bytes)

        channel.write(writeSizeBuffer)
        channel.write(buffer)
    }

    fun readString() : String {
        return String(readBytes(), UTF_8)
    }

    fun readBytes() : ByteArray {

        readSizeBuffer.clear()
        channel.read(readSizeBuffer)
        readSizeBuffer.rewind()

        val size = readSizeBuffer.int
        val buffer = ByteBuffer.allocate(size)
        channel.read(buffer)
        buffer.rewind()

        return buffer.array()
    }

    fun close() {
        channel.close()
    }

}