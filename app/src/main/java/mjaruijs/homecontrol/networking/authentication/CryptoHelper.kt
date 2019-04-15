package mjaruijs.homecontrol.networking.authentication

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.DIGEST_SHA256
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import java.security.*

class CryptoHelper {

    private fun getKeyStore(): KeyStore {
        return try {
            KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            throw RuntimeException(e.message)
        }
    }

    fun createKeyPair() {
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")

        generator.initialize(
                KeyGenParameterSpec.Builder("Galaxy S8", PURPOSE_SIGN)
                        .setKeySize(1024)
                        .setDigests(DIGEST_SHA256)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .setUserAuthenticationRequired(true)
                        .build())

        generator.generateKeyPair()
    }

    private fun getPrivateKey(): PrivateKey {
        val store = getKeyStore()
        store.load(null)
        return store.getKey("Galaxy S8", null) as PrivateKey
    }

    fun getPublicKey(): PublicKey {
        val store = getKeyStore()
        store.load(null)
        return store.getCertificate("Galaxy S8").publicKey
    }

    fun getSignature(): Signature {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(getPrivateKey())
        return signature
    }

}