package com.aioapp.nuggetmvp.service.camerapicture

class GetBackStateFlags {
    private var isLocationFound = false
    private var isPhotoCaptured = false
    private var isTheftTriggered = false
    private var isEmailSent = false
    private var isSmsSent = false
    private var isDataDeleted = false
    private var isScreenOn = true
    private var isTriggerCommandReceived = false
    private var isNetworkAvailable = false

    init {
        reset()
    }

    override fun toString(): String {
        return ("isLocationFound = " + isLocationFound + ", isPhotoCaptured = "
                + isPhotoCaptured + ", isTheftTriggered = " + isTheftTriggered
                + ", isEmailSent = " + isEmailSent + ", isSmsSent = "
                + isSmsSent + ", isDataDeleted = " + isDataDeleted
                + ", isScreenOn = " + isScreenOn + ", isRcvdCommand_1 = "
                + isTriggerCommandReceived + ", isNetworkAvailable = "
                + isNetworkAvailable)
    }

    private fun reset() {
        isNetworkAvailable = false
        isTriggerCommandReceived = isNetworkAvailable
        isDataDeleted = isTriggerCommandReceived
        isSmsSent = isDataDeleted
        isEmailSent = isSmsSent
        isTheftTriggered = isEmailSent
        isPhotoCaptured = isTheftTriggered
        isLocationFound = isPhotoCaptured
        isScreenOn = true
    }
}