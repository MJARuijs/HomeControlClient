package mjaruijs.servertest.activities.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.login_activity.*
import mjaruijs.servertest.R
import mjaruijs.servertest.activities.MainActivity
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.authentication.AccessConnection
import mjaruijs.servertest.server.authentication.ConnectionState
import mjaruijs.servertest.server.authentication.ConnectionState.*
import java.security.*
import java.security.spec.ECGenParameterSpec

class LoginActivity : AppCompatActivity() {

    private var keyGuardManager: KeyguardManager? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyPairGenerator? = null
    private val signature = Signature.getInstance("SHA256withECDSA")

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

        keyGuardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        setOnClickListeners()

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        try {
            keyGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get an instance of KeyGenerator", e)
        }

        if (!keyGuardManager!!.isKeyguardSecure) {
            Toast.makeText(this, "Lock screen is not secure..", LENGTH_SHORT).show()

            fingerprint_frame.isEnabled = false
            button_fingerprint.isEnabled = false
            return
        }
        createKey()
    }

    private fun setOnClickListeners() {

        fingerprint_frame.setOnClickListener { _ ->
            run {
                val fingerprintDialog = FingerprintDialog()
                if (initSignature()) {
                    fingerprintDialog.cryptoObject = FingerprintManager.CryptoObject(signature)
                    fingerprintDialog.show(fragmentManager, "FingerprintDialog")
                }
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
        gridTranslation.duration = 5000
        arrowTranslation.duration = 5000

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

    private fun createKey() {
        try {
            keyStore?.load(null)

            keyGenerator?.initialize(KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                        .setUserAuthenticationRequired(true)
                        .build())
            keyGenerator?.generateKeyPair()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private fun initSignature(): Boolean {
        return try {
            keyStore?.load(null)
            val privateKey = keyStore?.getKey(KEY_NAME, null) as PrivateKey
            signature.initSign(privateKey)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            e.printStackTrace()
            false
        } catch (e: UserNotAuthenticatedException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
        const val KEY_NAME = "fingerprint_key"
    }

}
