package mjaruijs.servertest.activities.login

import android.app.DialogFragment
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fingerprint_dialog_content.*
import mjaruijs.servertest.R
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.authentication.EnrollFingerConnection

class FingerprintDialog : DialogFragment() {

    var cryptoObject: FingerprintManager.CryptoObject? = null
    private var handler: FingerprintHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = FingerprintHandler(activity.getSystemService(FingerprintManager::class.java) as FingerprintManager)
        retainInstance = true
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog_NoActionBar)

        enrollFingerprint()
    }

    private fun enrollFingerprint() {
        EnrollFingerConnection(object: ConnectionResponse() {

        }).execute()
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
        if (cryptoObject != null) {
            handler?.startListening(cryptoObject)
        }
    }

    override fun onPause() {
        super.onPause()
        handler?.stopListening()
    }

}