package mjaruijs.homecontrol.networking

import android.content.Context
import android.content.Intent
import android.util.SparseArray
import mjaruijs.homecontrol.Constants.serverIP
import mjaruijs.homecontrol.Constants.serverPort
import mjaruijs.homecontrol.networking.client.SecureClient
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

object NetworkManager {

    private lateinit var client: SecureClient

    private val networks = ArrayList<String>()
    private var queue = LinkedList<String>()
    private var pendingRequests = SparseArray<String>()

    private val clientInitialized = AtomicBoolean(false)
    private val initializing = AtomicBoolean(false)


    fun addNetworks(vararg ssids: String) = networks.addAll(ssids)

    fun addMessage(requester: String, message: String) {
        println("ADDING MESSAGE TO WRITE: $message :: $requester :: ${queue.size}")
//        if (clientInitialized.get()) {
            val messageId = System.nanoTime().toInt()
            queue.addLast("id=$messageId;$message")
            pendingRequests.append(messageId, requester)
//        }
    }

    fun ready() = clientInitialized.get()

    fun addSendOnlyMessage(message: String) {
        if (clientInitialized.get()) {
            queue.addLast(message)
        } else {
            Thread {
                client.writeMessage(message)
            }
        }
    }

    fun stopClient() {
        clientInitialized.set(false)
        println(clientInitialized.get())
    }

    fun run(context: Context) {
        start(context)
        read(context)
        write(context)
    }

    private fun start(context: Context) {
        if (clientInitialized.get()) {
            println("INITIALIZED")
            return
        }

        Thread {
            try {
                println("STARTING CONNECTION")
                initializing.set(true)
                client = SecureClient(serverIP, serverPort)
                clientInitialized.set(true)
            } catch (e: Exception) {
                println("CONNECTION FAILED ")
                clientInitialized.set(false)
                start(context)
            } finally {
                initializing.set(false)
            }
        }.start()
    }

    private fun read(context: Context) {
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

//                    clientInitialized.set(false)
//                    start()
//                    read(context)
                }

            }
        }.start()
    }

    private fun write(context: Context) {
        Thread {
            if (!clientInitialized.get() && !initializing.get()) {
                start(context)
            }

            while (!clientInitialized.get()) {}

            while (clientInitialized.get()) {
                if (queue.isNotEmpty()) {
                    val message = queue.pop()

                    try {
                        println("WRITING $message")
                        client.writeMessage(message)
                    } catch (e: Exception) {
                        println("EXCEPTION")
                        clientInitialized.set(false)
                        queue.addFirst(message)
                        run(context)
//                        start()
//                        write(context)
                    }
                }
            }
        }.start()
    }

}
