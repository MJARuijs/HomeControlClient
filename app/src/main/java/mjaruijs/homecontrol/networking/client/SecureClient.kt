package mjaruijs.homecontrol.networking.client

import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets.UTF_8
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class SecureClient(channel: SocketChannel): Client(channel) {

    private companion object {
        val symmetricGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
        val asymmetricGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")

        init {
            symmetricGenerator.init(128)
            asymmetricGenerator.initialize(2048, SecureRandom.getInstance("SHA1PRNG"))
        }
    }

    private val encryptor = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    private val decryptor = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    private val symmetricKey: SecretKey

    constructor(host: String, port: Int): this(SocketChannel.open(InetSocketAddress(host, port)))

    init {
        symmetricKey = symmetricGenerator.generateKey()

        val keyPair = asymmetricGenerator.generateKeyPair()
        val clientKey = keyPair.private

        write(keyPair.public.encoded)

        val keyFactory = KeyFactory.getInstance("RSA")
        val serverKey = keyFactory.generatePublic(X509EncodedKeySpec(read()))

        encryptor.init(Cipher.PUBLIC_KEY, serverKey)
        decryptor.init(Cipher.PRIVATE_KEY, clientKey)
    }

    fun readMessage(): String {
        return try {
            val message = read()
            val key = read()

            val decryptedKey = decryptor.doFinal(key)

            val secretKey = SecretKeySpec(decryptedKey, 0, decryptedKey.size, "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val decryptedMessage = cipher.doFinal(message)

            String(decryptedMessage, UTF_8)
        } catch (e: Exception) {
            "CONNECTION_LOST"
        }

    }

    @Throws (Exception::class)
    fun writeMessage(message: String) {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey)
        val messageBytes = cipher.doFinal(message.toByteArray(UTF_8))

        val keyBytes = encryptor.doFinal(symmetricKey.encoded)

        write(messageBytes)
        write(keyBytes)
    }

}