package mjaruijs.servertest.networking.authentication

import android.hardware.fingerprint.FingerprintManager.CryptoObject
import android.util.Base64
import kotlin.text.Charsets.UTF_8

class Signer(private val cryptoObject: CryptoObject) {

    fun signRequest(data: String): String {
        cryptoObject.signature.update(data.toByteArray(UTF_8))
        val bytes = cryptoObject.signature.sign()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}