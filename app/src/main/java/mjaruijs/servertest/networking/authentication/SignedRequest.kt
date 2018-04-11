package mjaruijs.servertest.networking.authentication

data class SignedRequest(private val enrollment: Enrollment, private val signedMessage: String) {

    override fun toString(): String {
        return "$enrollment"
    }
}