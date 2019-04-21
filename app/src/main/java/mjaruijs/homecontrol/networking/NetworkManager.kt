package mjaruijs.homecontrol.networking

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.SparseArray
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.client.SecureClient
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

object NetworkManager {

    private lateinit var client: SecureClient
    private lateinit var wifiManager: WifiManager

    private val networks = ArrayList<String>()
    private var queue = LinkedList<String>()
    private var pendingRequests = SparseArray<String>()

    private val clientInitialized = AtomicBoolean(false)


    fun addNetworks(vararg ssids: String) = networks.addAll(ssids)

    fun addMessage(requester: String, message: String) {
        println("ADDING MESSAGE TO WRITE: $message :: $requester :: ${queue.size}")
        if (clientInitialized.get()) {
            val messageId = System.nanoTime().toInt()
            queue.addLast("id=$messageId;$message")
            pendingRequests.append(messageId, requester)
        }
    }

    fun ready() = clientInitialized.get()

    fun test(message: String, context: Context) {
        Thread {
            startClient(context)
            while (!clientInitialized.get()) {}
            addMessage("", message)
            stopClient()
        }.start()
    }

    fun addSendOnlyMessage(message: String) {
        if (clientInitialized.get()) {
            queue.addLast(message)
        } else {
            Thread {
                client.writeMessage(message)
            }
        }
    }

    fun isConnectedToHomeNetwork(): Boolean {
        val networkInfo = wifiManager.connectionInfo

        if (networkInfo != null) {
            val ssid = networkInfo.ssid.replace("\"", "")
            return networks.contains(ssid)
        }

        return false
    }

    fun stopClient() {
        clientInitialized.set(false)
        println(clientInitialized.get())
    }

    fun startClient(context: Context) {
        if (clientInitialized.get()) return

        wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager

        Thread {
            try {
//                client = MessageClient(serverIP, serverPort)
                client = SecureClient(serverIP, serverPort)
                clientInitialized.set(true)
            } catch (e: Exception) {

            }
        }.start()

        Thread {
            while (!clientInitialized.get()) {}

            while (clientInitialized.get()) {
                if (queue.isNotEmpty()) {
                    try {
                        val message = queue.pop()
                        println("WRITING $message")
                        client.writeMessage(message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()

        Thread {
            while (!clientInitialized.get()) {}

            while (clientInitialized.get()) {
                try {
                    val message = client.readMessage()

                    if (message != "") {
                        println("NetworkManager: $message")
                        val startIndex = message.indexOf("id=") + 3
                        val endIndex = message.indexOf(';')
                        val id = message.substring(startIndex, endIndex).toInt()
                        val requester = pendingRequests[id]

                        val intent = Intent("mjaruijs.home_control.FROM_SERVER_TO_$requester")
                        intent.putExtra("message", message)
                        context.sendBroadcast(intent)
                    }
                } catch (e: Exception) {

                }

            }
        }.start()
    }

}
