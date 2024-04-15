package com.aioapp.nuggetmvp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepository
import com.aioapp.nuggetmvp.di.usecase.RefillUseCase
import com.aioapp.nuggetmvp.service.camera.CameraControllerWithoutPreview
import com.aioapp.nuggetmvp.service.camera.IFrontCaptureCallback
import com.aioapp.nuggetmvp.service.constants.RECORDER_RUNNING_NOT_IF_ID
import com.aioapp.nuggetmvp.utils.actionCallBack
import com.aioapp.nuggetmvp.utils.imageSavedToGalleryCallBack
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NuggetCameraService : Service(), IFrontCaptureCallback {
    private var ccv2WithoutPreview: CameraControllerWithoutPreview? = null

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var refillUseCase: RefillUseCase

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
        actionCallBack = {
            Log.e("onStartCommand", "onStartCommand:--->ActionCallBackHere ")
            capturePhoto()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ccv2WithoutPreview?.closeCamera()
        ccv2WithoutPreview = null
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
        filePath?.let { imageSavedToGalleryCallBack?.invoke(it) }
//        //  sendRefillApiCall(filePath)
//        CoroutineScope(IO).launch {
//            delay(12000)
//            withContext(Main) {
//                capturePhoto()
//            }
//        }
    }

}