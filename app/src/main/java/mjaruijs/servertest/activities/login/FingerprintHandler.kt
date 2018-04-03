package mjaruijs.servertest.activities.login

import android.hardware.fingerprint.FingerprintManager
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult
import android.hardware.fingerprint.FingerprintManager.CryptoObject
import android.os.CancellationSignal

class FingerprintHandler(private val fingerprintManager: FingerprintManager,
                         private val callback: Callback) {

    private var cancellationSignal: CancellationSignal? = null
    private var selfCancelled: Boolean = false

    fun startListening() {
        if (!isFingerprintSensorAvailable()) {
            return
        }

        cancellationSignal = CancellationSignal()

        val authenticationCallback = object: FingerprintManager.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                callback.onSuccess(result.cryptoObject)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (!selfCancelled) {
                    callback.onError(errString.toString(), true)
                }
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
                callback.onError(helpString.toString(), false)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onError("Fingerprint not recognized..")
            }

        }
        fingerprintManager.authenticate(callback.cryptoObject, cancellationSignal, 0, authenticationCallback, null)
    }

    fun stopListening() {
        selfCancelled = true
        cancellationSignal?.cancel()
        cancellationSignal = null
    }

    private fun isFingerprintSensorAvailable(): Boolean {
        return fingerprintManager.isHardwareDetected and fingerprintManager.hasEnrolledFingerprints()
    }

    interface Callback {

        val cryptoObject: CryptoObject
        fun onSuccess(cryptoObject: CryptoObject)
        fun onError(errorMessage: String, isHardError: Boolean)
        fun onError(errorMessage: String)

    }

}