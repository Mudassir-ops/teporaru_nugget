package com.aioapp.nuggetmvp.service.constants


import android.os.Looper

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}

const val DEFAULT_BITRATE = 128000
const val SAMPLE_RATE = 16000
const val RECORDING_RUNNING = 0
const val RECORDING_STOPPED = 1
const val RECORDER_RUNNING_NOT_IF_ID = 10000
const val AMPLITUDE_UPDATE_MS = 50L