package mjaruijs.homecontrol.networking.server.command

enum class CommandResult {

    SUCCESS,

    FAILED,

    MAIN_POWER_OFF,

    PC_STILL_ON,

    SESSION_EXPIRED,

    NO_CONNECTION

}
