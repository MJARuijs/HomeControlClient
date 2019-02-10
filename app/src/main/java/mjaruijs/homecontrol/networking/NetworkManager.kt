package mjaruijs.homecontrol.networking

import android.content.Context
import android.net.wifi.WifiManager

object NetworkManager {

    private val networks = ArrayList<String>()

    fun addNetworks(vararg ssids: String) = networks.addAll(ssids)

    fun removeNetworks(vararg ssids: String) {
        for (ssid in ssids) {
            networks.removeIf { networks.contains(ssid) }
        }
    }

    fun isConnectedToHomeNetwork(context: Context): Boolean {

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val networkInfo = wifiManager.connectionInfo

        if (networkInfo != null) {
            val ssid = networkInfo.ssid.replace("\"", "")
            return networks.contains(ssid)
        }

        return false
    }

}