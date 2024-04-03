package com.aioapp.nuggetmvp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepository
import com.aioapp.nuggetmvp.service.constants.AMPLITUDE_UPDATE_MS
import com.aioapp.nuggetmvp.service.constants.RECORDER_RUNNING_NOT_IF_ID
import com.aioapp.nuggetmvp.service.constants.RECORDING_RUNNING
import com.aioapp.nuggetmvp.service.constants.RECORDING_STOPPED
import com.aioapp.nuggetmvp.service.constants.createFilePathInCacheDirectory
import com.aioapp.nuggetmvp.service.constants.ensureBackgroundThread
import com.aioapp.nuggetmvp.service.helper.Events
import com.aioapp.nuggetmvp.service.recorder.MediaNuggetRecorderWrapper
import com.aioapp.nuggetmvp.service.recorder.NuggetRecorder
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class NuggetRecorderService : Service() {
    @Inject
    lateinit var notificationRepository: NotificationRepository

    companion object {
        var isRunning = false
    }

    private var currFilePath = ""
    private var duration = 0
    private var status = RECORDING_STOPPED
    private var amplitudeTimer: Timer? = null
    private var nuggetRecorder: NuggetRecorder? = null
    override fun onCreate() {
        super.onCreate()
        val notificationBuilder = notificationRepository.buildNotification()
        val notification = notificationBuilder.build()
        startForeground(RECORDER_RUNNING_NOT_IF_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startRecording()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        isRunning = false
    }

    private fun startRecording() {
        isRunning = true
        if (status == RECORDING_RUNNING) {
            return
        }
        currFilePath = this@NuggetRecorderService.createFilePathInCacheDirectory().toString()
        try {
            if (File(currFilePath).exists()) {
                nuggetRecorder = MediaNuggetRecorderWrapper()
                nuggetRecorder?.setOutputFile(currFilePath)
                nuggetRecorder?.prepare()
                nuggetRecorder?.start()
                duration = 0
                status = RECORDING_RUNNING
                broadcastRecorderInfo()
                startAmplitudeUpdates()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // stopRecording()
        }
    }

    private fun stopRecording() {
        amplitudeTimer?.cancel()
        status = RECORDING_STOPPED
        nuggetRecorder?.apply {
            try {
                stop()
                release()
                ensureBackgroundThread {
                    EventBus.getDefault().post(Events.RecordingSaved(currFilePath))
                }
            } catch (e: Exception) {
                Toast.makeText(this@NuggetRecorderService, "${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        nuggetRecorder = null
    }

    private fun broadcastRecorderInfo() {
        startAmplitudeUpdates()
    }

    private fun startAmplitudeUpdates() {
        amplitudeTimer?.cancel()
        amplitudeTimer = Timer()
        amplitudeTimer?.scheduleAtFixedRate(getAmplitudeUpdateTask(), 0, AMPLITUDE_UPDATE_MS)
    }

    private fun getAmplitudeUpdateTask() = object : TimerTask() {
        override fun run() {
            if (nuggetRecorder != null) {
                try {
                    EventBus.getDefault().post(nuggetRecorder?.getMaxAmplitude()
                        ?.let { Events.RecordingAmplitude(it) })
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }
        }
    }
}
