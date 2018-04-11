package mjaruijs.servertest.networking.networking

import java.nio.charset.StandardCharsets.UTF_8
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class SecureClient(private val client: Client) {

    private companion object {
        val generator = KeyPairGenerator.getInstance("RSA")!!

        init {
            generator.initialize(1024, SecureRandom.getInstanceStrong())
        }
    }

    private val encryptor = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    private val decryptor = Cipher.getInstance("RSA/ECB/PKCS1Padding")

    constructor(host: String, port: Int): this(Client(host, port))

    init {
        val keyPair = generator.generateKeyPair()
        val clientKey = keyPair.private

        client.write(keyPair.public.encoded)

        val keyFactory = KeyFactory.getInstance("RSA")
        val serverKey = keyFactory.generatePublic(X509EncodedKeySpec(client.read().array()))

        encryptor.init(Cipher.ENCRYPT_MODE, serverKey)
        decryptor.init(Cipher.DECRYPT_MODE, clientKey)
    }

    fun readMessage(): String {
        val bytes = client.read().array()
        return String(decryptor.doFinal(bytes), UTF_8)
    }

    fun writeMessage(message: String) {
        val bytes = message.toByteArray(UTF_8)
        client.write(encryptor.doFinal(bytes))
    }

    fun write(bytes: ByteArray) {
        client.write(encryptor.doFinal(bytes))
    }

    fun close() {
        client.close()
    }

}