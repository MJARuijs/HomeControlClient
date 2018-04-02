package mjaruijs.servertest.server

abstract class ConnectionResponse {

    open fun commandResult(result: CommandResult) {}

    open fun resultState(result: ConnectionState) {}

    fun resultBoolean(result: Boolean) {}

    open fun resultBooleanArray(result: BooleanArray) {}
}
