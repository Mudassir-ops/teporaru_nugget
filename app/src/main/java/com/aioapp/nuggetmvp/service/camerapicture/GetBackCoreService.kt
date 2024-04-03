package com.aioapp.nuggetmvp.service.camerapicture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aioapp.nuggetmvp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetBackCoreService : Service(), IFrontCaptureCallback {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= 26) {
            if (Build.VERSION.SDK_INT > 26) {
                val CHANNEL_ONE_ID = ".com.example.service"
                val CHANNEL_ONE_NAME = "hidden camera"
                var notificationChannel: NotificationChannel? = null
                notificationChannel = NotificationChannel(
                    CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.setShowBadge(true)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                val manager =
                    applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(notificationChannel)
                val icon =
                    BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
                val notification =
                    Notification.Builder(applicationContext).setChannelId(CHANNEL_ONE_ID)
                        .setContentTitle("Service")
                        .setContentText("Capture picture using foreground service")
                        .setSmallIcon(R.drawable.ic_launcher_foreground).setLargeIcon(icon).build()
                val notificationIntent = Intent(applicationContext, GetBackCoreService::class.java)
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                notification.contentIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                startForeground(104, notification)
            } else {
                startForeground(104, updateNotification())
            }
        } else {
            val notification = NotificationCompat.Builder(this).setContentTitle("Service")
                .setContentText("Capture picture using foreground service")
                .setSmallIcon(R.drawable.ic_launcher_foreground).setOngoing(true).build()
            startForeground(104, notification)
        }
        Log.e("onStartCommand", "onStartCommand: ")

        CoroutineScope(Dispatchers.Default).launch {
            delay(30000)
            withContext(Main) {
                capturePhoto()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        //  adHandler.removeCallbacks(adRunnable)
        stopSelf()
        Utils.LogUtil.LogD(Constants.LOG_TAG, "Service Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun capturePhoto() {
        val frontCapture = CameraView(this@GetBackCoreService.baseContext)
        frontCapture.capturePhoto(this@GetBackCoreService)
    }

    override fun onPhotoCaptured(filePath: String?) {
//        sendDataToFragment(filePath)
        Log.e("FilePathAfterDone--->", "onPhotoCaptured: $filePath")
        CoroutineScope(IO).launch {
            delay(10000)
            withContext(Main) {
                capturePhoto()
            }
        }
    }

    private fun sendDataToFragment(data: String?) {
        Log.e("FilePathAfterDone--->", "sendDataToFragment: $data")
        val intent = Intent()
        intent.setAction("MY_ACTION")
        intent.putExtra("data", data)
        sendBroadcast(intent)
    }

    override fun onCaptureError(errorCode: Int) {}
    private fun updateNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, GetBackCoreService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        return NotificationCompat.Builder(this).setTicker("Ticker").setContentTitle("Service")
            .setContentText("Capture picture using foreground service")
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent)
            .setOngoing(true).build()
    }

}