package com.aioapp.nuggetmvp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepository
import com.aioapp.nuggetmvp.service.camera.CameraControllerWithoutPreview
import com.aioapp.nuggetmvp.service.camera.IFrontCaptureCallback
import com.aioapp.nuggetmvp.service.constants.RECORDER_RUNNING_NOT_IF_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        ccv2WithoutPreview = CameraControllerWithoutPreview(applicationContext)

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
        if (ccv2WithoutPreview?.isSessionClosed() == true) {
            ccv2WithoutPreview?.openCamera()
        }
        ccv2WithoutPreview?.takePicture(this@NuggetCameraService)
    }

    override fun onPhotoCaptured(filePath: String?) {
        iamgeCount++
        Log.wtf("OnPhotoCaptured--->", "$iamgeCount--->onPhotoCaptured:$filePath ")
        CoroutineScope(IO).launch {
            delay(5000)
            withContext(Main) {
                capturePhoto()
            }
        }
    }
}