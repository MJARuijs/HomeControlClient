package mjaruijs.servertest.activities.login

import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.util.Log

class FingerprintHandler(var fingerprintManager: FingerprintManager) : FingerprintManager.AuthenticationCallback() {

    private var cancellationSignal: CancellationSignal? = null
    private var selfCancelled: Boolean? = null

    fun startListening(cryptoObject: FingerprintManager.CryptoObject?) {
        if (!isFingerprintSensorAvailable()) {
            return
        }

        cancellationSignal = CancellationSignal()
        selfCancelled = false

        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    fun stopListening() {
        selfCancelled = true
        cancellationSignal?.cancel()
        cancellationSignal = null
    }

    private fun isFingerprintSensorAvailable(): Boolean {
        return fingerprintManager.isHardwareDetected and fingerprintManager.hasEnrolledFingerprints()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        if (!selfCancelled!!) {
            Log.i("HANDLER", "error")
        }
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        Log.i("HANDLER", "YAS")
    }

}