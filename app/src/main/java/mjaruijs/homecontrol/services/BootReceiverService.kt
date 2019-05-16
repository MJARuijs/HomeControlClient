package mjaruijs.homecontrol.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiverService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("BOOT RECEIVED")
//        AppCardInfo.getAppCards(context)
    }
}