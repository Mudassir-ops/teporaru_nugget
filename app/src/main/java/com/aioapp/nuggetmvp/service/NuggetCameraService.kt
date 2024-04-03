package com.aioapp.nuggetmvp.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepository
import com.aioapp.nuggetmvp.service.camera.CameraControllerWithoutPreview
import com.aioapp.nuggetmvp.service.camera.IFrontCaptureCallback
import com.aioapp.nuggetmvp.service.constants.RECORDER_RUNNING_NOT_IF_ID
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class NuggetCameraService : Service(), IFrontCaptureCallback {
    @Inject
    lateinit var notificationRepository: NotificationRepository
    private var ccv2WithoutPreview: CameraControllerWithoutPreview? = null
    private var timer: Timer? = null
    override fun onCreate() {
        super.onCreate()
        val notificationBuilder = notificationRepository.buildNotification()
        val notification = notificationBuilder.build()
        startForeground(RECORDER_RUNNING_NOT_IF_ID, notification)
        ccv2WithoutPreview = CameraControllerWithoutPreview(applicationContext)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("onStartCommand", "onStartCommand: ")
        ccv2WithoutPreview?.openCamera()
        ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        stopCaptureTimer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startCaptureTimer() {
        timer = Timer()
        timer?.schedule(timerTask {


        }, 0, 5000)
    }

    private fun stopCaptureTimer() {
        timer?.cancel()
        timer = null
    }

    private fun capturePhoto() {
        ccv2WithoutPreview?.openCamera()
        if (ccv2WithoutPreview?.isCaptureSessionActive() == true) {
            ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
        } else {
            Log.e("CapturePhoto", "Capture session is closed. Reopening...")
            ccv2WithoutPreview?.openCamera()
            ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
//            Handler(Looper.getMainLooper()).postDelayed({
//                ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
//            }, 1000) // Delay for 1 second (adjust as needed)
        }
    }

    override fun onPhotoCaptured(filePath: String?) {
        ccv2WithoutPreview?.closeCamera()
        Log.wtf("OnPhotoCaptured--->", "onPhotoCaptured:$filePath ")
    }

    private val handler = Handler(Looper.getMainLooper())
}