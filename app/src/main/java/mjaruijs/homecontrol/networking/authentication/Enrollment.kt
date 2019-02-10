package mjaruijs.homecontrol.networking.authentication

class Enrollment(private val username: String,
                 private val deviceID: String,
                 private val publicKey: String,
                 private val timeStamp: Long = System.currentTimeMillis()) {

    override fun toString(): String {
        return listOf(username, deviceID, publicKey, timeStamp.toString()).joinToString("|")
    }
}