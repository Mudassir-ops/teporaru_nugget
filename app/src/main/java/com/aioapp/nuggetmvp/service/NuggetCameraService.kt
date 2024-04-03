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
import javax.inject.Inject

@AndroidEntryPoint
class NuggetCameraService : Service(), IFrontCaptureCallback {
    private var ccv2WithoutPreview: CameraControllerWithoutPreview? = null

    private var iamgeCount = 0

    @Inject
    lateinit var notificationRepository: NotificationRepository
    override fun onCreate() {
        super.onCreate()
        val notificationBuilder = notificationRepository.buildNotification()
        val notification = notificationBuilder.build()
        startForeground(RECORDER_RUNNING_NOT_IF_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("onStartCommand", "onStartCommand: ")
        capturePhoto()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun capturePhoto() {
        Handler(Looper.getMainLooper()).postDelayed({
            ccv2WithoutPreview = CameraControllerWithoutPreview(applicationContext)
            ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
        }, 5000)
    }

    override fun onPhotoCaptured(filePath: String?) {
        ccv2WithoutPreview = null
        iamgeCount++
        capturePhoto()
        Log.wtf("OnPhotoCaptured--->", "$iamgeCount--->onPhotoCaptured:$filePath ")
    }

//    private fun capturePhoto() {
//        if (ccv2WithoutPreview?.isSessionClosed() == true) {
//            ccv2WithoutPreview?.openCamera()
//        }
//        ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
//    }
//
//    override fun onPhotoCaptured(filePath: String?) {
//        imageCount++
//        Log.wtf("OnPhotoCaptured--->", "$imageCount--->onPhotoCaptured:$filePath ")
//        capturePhoto()
//    }
}