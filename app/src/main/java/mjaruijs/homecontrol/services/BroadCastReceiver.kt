package mjaruijs.homecontrol.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadCastReceiver(private val requestingClass: String, private val onRead: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        if (action == "mjaruijs.home_control.FROM_SERVER_TO_$requestingClass") {
            val message = intent.getStringExtra("message") ?: return
            onRead(message)
        }

    }
}