package mjaruijs.homecontrol.networking.server

import mjaruijs.homecontrol.networking.server.authentication.AuthenticationResult
import mjaruijs.homecontrol.networking.server.command.CommandResult

abstract class ConnectionResponse {

    open fun config(result: Any) {}

    open fun commandResult(result: CommandResult) {}

    open fun authenticationResult(result: AuthenticationResult) {}

    open fun resultBooleanArray(result: BooleanArray) {}

//    open fun passThroughClient(result: ConnectionResult) {}
}

