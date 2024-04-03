package com.aioapp.nuggetmvp.di.repositories.notification

import androidx.core.app.NotificationCompat

interface NotificationRepository {
    fun buildNotification(): NotificationCompat.Builder
    fun updateNotification(value: Int)
}