package com.aioapp.nuggetmvp.di.repositories.notification


import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject

class NotificationRepositoryImp @Inject constructor(
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat
) : NotificationRepository {
    override fun buildNotification(): NotificationCompat.Builder {
        return notificationBuilder
    }

    /**
     * Targeting Android 11 no need to Handle Post Notification
     */
    @SuppressLint("MissingPermission")
    override fun updateNotification(value: Int) {
        val notification = buildNotificationWithCount(value)
        notificationManager.notify(1, notification.build())
    }

    private fun buildNotificationWithCount(value: Int): NotificationCompat.Builder {
        val contentText = "You have $value updates "
        return notificationBuilder.setContentText(contentText).setSound(null)
    }
}