package mjaruijs.homecontrol.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.login_activity.*
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.dialogs.FingerprintDialog
import mjaruijs.homecontrol.activities.dialogs.FingerprintDialog.FingerDialogCallback
import mjaruijs.homecontrol.networking.MessageSender
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.networking.authentication.Signer
import mjaruijs.homecontrol.networking.server.authentication.*
import mjaruijs.homecontrol.services.NotificationListener
import android.R.attr.delay
import android.R.id.message
import android.app.Notification
import android.app.NotificationChannel
import android.graphics.Color
import android.support.v4.app.NotificationManagerCompat


class LoginActivity : AppCompatActivity() {

    private val cryptoHelper = CryptoHelper()

    private var keypadArrowDown: AnimatedVectorDrawable? = null
    private var keypadArrowUp: AnimatedVectorDrawable? = null
    private var keypadShowing = true
    private lateinit var notificationListener: Intent

    init {
        NetworkManager.addNetworks("Alpha Network", "devolo-17f")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        inputField.setOnTouchListener{ _, _ -> true }

        notificationListener = Intent(applicationContext, NotificationListener::class.java)
        keypadArrowDown = getDrawable(R.drawable.keyboard_arrow_animation) as AnimatedVectorDrawable
        keypadArrowUp = getDrawable(R.drawable.keypad_arrow_animation) as AnimatedVectorDrawable

        hideKeypadButton.setImageDrawable(keypadArrowDown)
        setOnClickListeners()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("HOME_CONTROL", "Home Control", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
        startService(notificationListener)

        Toast.makeText(this, "HALLO", LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()
        startService(notificationListener)
    }

    override fun onResume() {
        super.onResume()



//        manager.notify(System.currentTimeMillis().toInt(), notification.build())
//        Toast.makeText(this, "HALLO", LENGTH_SHORT).show()
        startService(notificationListener)
    }

    private fun setOnClickListeners() {

        fingerprint_frame.setOnClickListener {
           registerToBackend()
        }

        button_delete.setOnClickListener {
            val length = inputField.text.length
            inputField.text.delete(length - 1, length)
        }

        hideKeypadButton.setOnClickListener { toggleKeypad() }

        button_submit.setOnClickListener {
//            startActivity(Intent(applicationContext, MainActivity::class.java))

            MessageSender(object : MessageSender.ConnectionResponse {
                override fun result(message: String) {
                    when (message) {
                        "ACCESS_GRANTED" -> startActivity(Intent(applicationContext, MainActivity::class.java))
                        "ACCESS_DENIED" -> Toast.makeText(applicationContext, "Password is incorrect!", Toast.LENGTH_LONG).show()
                        "NO_CONNECTION" -> Toast.makeText(applicationContext, "No connection!", Toast.LENGTH_LONG).show()
                        "CONNECTION_CLOSED" -> Toast.makeText(applicationContext, "Connection Closed!", LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }).execute(inputField.text.toString())
        }
    }

    fun numberClick(view: View) {
        if (inputField.text.length < 128) {
            inputField.append(view.tag?.toString())
        }
    }

    private fun toggleKeypad() {
        val gridPosition = if (keypadShowing) 2960f else 1216f
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
//        val publicKey = cryptoHelper.getPublicKey()
//        val enrollment = Enrollment("Galaxy S8", "1", Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP))
//
//        val authenticationCallback = object: AuthenticationCallback {
//            override fun onAuthenticated(signer: Signer) {
//                EnrollFingerConnection(object: ConnectionResponse() {
//                    override fun authenticationResult(result: AuthenticationResult) {
//                        Toast.makeText(applicationContext, "Print enrolled!", LENGTH_SHORT).show()
//                    }
//                }).execute(SignedRequest(enrollment, signer.signRequest(enrollment.toString())))
//            }
//        }

        val fingerPrintDialog = FingerprintDialog()

        fingerPrintDialog.signature = signature
        fingerPrintDialog.callback = object: FingerDialogCallback {
            override fun onAuthenticated(signer: Signer) {

                // TODO("Create an animation that plays while waiting for the response from the server. After the server responds, we can close the dialog.")

                fingerPrintDialog.dismiss()
//                authenticationCallback.onAuthenticated(signer)
            }

        }
        fingerPrintDialog.show(fragmentManager, "FingerprintDialog")
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

//    interface AuthenticationCallback {
//        fun onAuthenticated(signer: Signer)
//    }
}
