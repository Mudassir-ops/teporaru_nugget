package com.aioapp.nuggetmvp.di.usecase


import androidx.core.app.NotificationCompat
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepository
import javax.inject.Inject

class NotificationUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    fun invokeBuildNotificationUseCase(): NotificationCompat.Builder {
        return notificationRepository.buildNotification()
    }
}