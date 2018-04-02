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
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.login_activity.*
import mjaruijs.servertest.R
import mjaruijs.servertest.activities.MainActivity
import mjaruijs.servertest.server.AccessConnection
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.ConnectionState
import mjaruijs.servertest.server.ConnectionState.*
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import javax.crypto.*

class LoginActivity : AppCompatActivity() {

    private var keyGuardManager: KeyguardManager? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null

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
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
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
        val defaultCipher: Cipher
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get an instance of Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get an instance of Cipher", e)
        }

        fingerprint_frame.setOnClickListener { _ ->
            run {
                val fingerprintDialog = FingerprintDialog()
                if (initCipher(defaultCipher)) {
                    fingerprintDialog.cryptoObject = FingerprintManager.CryptoObject(defaultCipher)
                    fingerprintDialog.show(fragmentManager, "FingerprintDialog")
                }
            }
        }

        button_delete.setOnClickListener { _ ->
            inputField.text = SpannableStringBuilder(inputField.text.substring(0, inputField.text.length - 1))
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
        inputField.append(view.tag?.toString())
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

            keyGenerator?.init(KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build())
            keyGenerator?.generateKey()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private fun initCipher(cipher: Cipher): Boolean {
        return try {
            keyStore?.load(null)
            val key = keyStore?.getKey(KEY_NAME, null)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            e.printStackTrace()
            false
        } catch (e: UserNotAuthenticatedException) {
            e.printStackTrace()
            false
        }
    }

    private fun tryEncrypt(cipher: Cipher) {
        try {
            val encrypted = cipher.doFinal(SECRET_BYTE_ARRAY)
            Toast.makeText(this, "TEST", Toast.LENGTH_LONG).show()
        } catch (e: BadPaddingException) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. " + "Retry the purchase", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.message)
        } catch (e: IllegalBlockSizeException) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. " + "Retry the purchase", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.message)
        }

    }

    private fun showAlreadyAuthenticated() {
        Toast.makeText(this, "Already in..", LENGTH_SHORT).show()
        val intent = keyGuardManager?.createConfirmDeviceCredentialIntent(null, null)
        if (intent != null) {
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(this, "nope4" , LENGTH_SHORT).show()

        }
    }

    private fun showAuthenticationScreen() {
        Toast.makeText(this, "Authenticate please..", LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val KEY_NAME = "fingerprint_key"
        private val SECRET_BYTE_ARRAY = byteArrayOf(1, 2, 3, 4, 5, 6)
    }

}
