package mjaruijs.servertest.authentication

data class SignedRequest(private val enrollment: Enrollment, private val signedMessage: String) {

    override fun toString(): String {
        return "$enrollment"
    }
}