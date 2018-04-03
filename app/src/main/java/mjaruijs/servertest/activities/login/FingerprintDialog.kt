package mjaruijs.servertest.activities.login

import android.app.DialogFragment
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.hardware.fingerprint.FingerprintManager.CryptoObject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.fingerprint_dialog_content.*
import mjaruijs.servertest.R
import mjaruijs.servertest.authentication.Signer
import java.security.Signature

class FingerprintDialog : DialogFragment(), FingerprintHandler.Callback {

    private var handler: FingerprintHandler? = null

    var callback: FingerDialogCallback? = null
    var signature: Signature? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog_NoActionBar)
        handler = FingerprintHandler(activity.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fingerprint_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        pulse_animator.start()
    }

    override fun onResume() {
        super.onResume()
        handler?.startListening()
    }

    override fun onPause() {
        super.onPause()
        handler?.stopListening()
    }

    override val cryptoObject: CryptoObject
        get() = FingerprintManager.CryptoObject(signature)

    override fun onSuccess(cryptoObject: CryptoObject) {
        callback?.onAuthenticated(Signer(cryptoObject))
    }

    override fun onError(errorMessage: String, isHardError: Boolean) {
        Toast.makeText(context, "Error $errorMessage", LENGTH_SHORT).show()
    }

    override fun onError(errorMessage: String) {
        onError(errorMessage, false)
    }

    interface FingerDialogCallback {
        fun onAuthenticated(signer: Signer)
    }
}