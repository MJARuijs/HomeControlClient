package mjaruijs.servertest.server

import mjaruijs.servertest.networking.networking.SecureClient
import mjaruijs.servertest.server.authentication.ConnectionState
import mjaruijs.servertest.server.command.CommandResult

abstract class ConnectionResponse {

    open fun commandResult(result: CommandResult) {}

    open fun resultState(result: ConnectionState) {}

    open fun resultBooleanArray(result: BooleanArray) {}

    open fun passThroughClient(result: SecureClient) {}
}
