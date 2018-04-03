package mjaruijs.servertest.activities.login

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

class Transaction(
        private val itemID: Long,
        private val userID: String,
        private val clientNonce: Long) {

    public fun toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()

        try {
            val dataOutputStream = DataOutputStream(outputStream)
            dataOutputStream.writeLong(itemID)
            dataOutputStream.writeUTF(userID)
            dataOutputStream.writeLong(clientNonce)
            return outputStream.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException(e.message)
        }
    }

}