package mjaruijs.servertest.activities.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.login_activity.*
import mjaruijs.servertest.R
import mjaruijs.servertest.activities.MainActivity
import mjaruijs.servertest.activities.login.FingerprintDialog.FingerDialogCallback
import mjaruijs.servertest.networking.authentication.Enrollment
import mjaruijs.servertest.networking.authentication.SignedRequest
import mjaruijs.servertest.networking.authentication.Signer
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.authentication.AccessConnection
import mjaruijs.servertest.server.authentication.ConnectionState
import mjaruijs.servertest.server.authentication.ConnectionState.*
import mjaruijs.servertest.server.authentication.CryptoHelper
import mjaruijs.servertest.server.authentication.EnrollFingerConnection

class LoginActivity : AppCompatActivity() {

    private val cryptoHelper = CryptoHelper()

    private var keypadArrowDown: AnimatedVectorDrawable? = null
    private var keypadArrowUp: AnimatedVectorDrawable? = null
    private var keypadShowing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        inputField.setOnTouchListener{ _, _ -> true }

        keypadArrowDown = getDrawable(R.drawable.keyboard_arrow_animation) as AnimatedVectorDrawable
        keypadArrowUp = getDrawable(R.drawable.keypad_arrow_animation) as AnimatedVectorDrawable

        hideKeypadButton.setImageDrawable(keypadArrowDown)
        setOnClickListeners()

//        registerToBackend()
    }

    private fun setOnClickListeners() {

        fingerprint_frame.setOnClickListener { _ ->
            run {
               registerToBackend()
            }
        }

        button_delete.setOnClickListener { _ ->
            val length = inputField.text.length
            inputField.text.delete(length - 1, length)
        }

        hideKeypadButton.setOnClickListener { _ -> toggleKeypad() }

        button_submit.setOnClickListener { _ ->
            run {
                AccessConnection(object : ConnectionResponse() {
                    override fun resultState(result: ConnectionState) {
                        when (result) {
                            ACCESS_GRANTED -> startActivity(Intent(applicationContext, MainActivity::class.java))
                            ACCESS_DENIED -> Toast.makeText(applicationContext, "Password is incorrect!", Toast.LENGTH_LONG).show()
                            NO_CONNECTION -> Toast.makeText(applicationContext, "No connection!", Toast.LENGTH_LONG).show()
                            CONNECTION_CLOSED -> TODO()
                        }
                    }
                }).execute(inputField.text.toString())
            }
        }
    }

    fun numberClick(view: View) {
        if (inputField.text.length < 128) {
            inputField.append(view.tag?.toString())
        }
    }

    private fun toggleKeypad() {
        val gridPosition = if (keypadShowing) 2860f else 1216f
        val arrowPosition = if (keypadShowing) 2764f else 1120f

        val gridTranslation: ObjectAnimator = ObjectAnimator.ofFloat(gridLayout, "y", gridPosition)
        val arrowTranslation: ObjectAnimator = ObjectAnimator.ofFloat(hideKeypadButton, "y", arrowPosition)
        gridTranslation.duration = 1500
        arrowTranslation.duration = 1500

        if (keypadShowing) {
            hideKeypadButton.setImageDrawable(keypadArrowDown)
            keypadArrowDown?.start()
        } else {
            hideKeypadButton.setImageDrawable(keypadArrowUp)
            keypadArrowUp?.start()
        }

        val morph = AnimatorSet()
        morph.playTogether(gridTranslation, arrowTranslation)
        morph.start()

        keypadShowing = !keypadShowing
    }

    private fun registerToBackend() {
        cryptoHelper.createKeyPair()
        val signature = cryptoHelper.getSignature()
        val publicKey = cryptoHelper.getPublicKey()
        val enrollment = Enrollment("Galaxy S8", "1", Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP))

        val authenticationCallback = object: AuthenticationCallback {
            override fun onAuthenticated(signer: Signer) {
                EnrollFingerConnection(object: ConnectionResponse() {
                    override fun resultState(result: ConnectionState) {
                        Toast.makeText(applicationContext, "Print enrolled!", LENGTH_SHORT).show()
                    }
                }).execute(SignedRequest(enrollment, signer.signRequest(enrollment.toString())))
            }
        }

        val fingerPrintDialog = FingerprintDialog()

        fingerPrintDialog.signature = signature
        fingerPrintDialog.callback = object: FingerDialogCallback {
            override fun onAuthenticated(signer: Signer) {

                //TODO("Create an animation that plays while waiting for the response from the server. After the server responds, we can close the dialog.")

                fingerPrintDialog.dismiss()
                authenticationCallback.onAuthenticated(signer)
            }

        }
        fingerPrintDialog.show(fragmentManager, "FingerprintDialog")
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

    interface AuthenticationCallback {
        fun onAuthenticated(signer: Signer)
    }
}
