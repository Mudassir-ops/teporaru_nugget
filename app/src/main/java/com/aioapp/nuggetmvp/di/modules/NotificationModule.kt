package com.aioapp.nuggetmvp.di.modules

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepositoryImp
import com.aioapp.nuggetmvp.utils.Constants.CHANNEL_ID
import com.aioapp.nuggetmvp.utils.Constants.CHANNEL_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerCompat {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this@apply)
            }
        }
        return notificationManager
    }

    @Singleton
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("Recording")
            .setSmallIcon(R.drawable.ic_launcher_background).setAutoCancel(true)
            .setSound(null).setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
    }

    /** Notification repository provider*/
    @Singleton
    @Provides
    fun provideNotificationRepository(
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat
    ): NotificationRepositoryImp {
        return NotificationRepositoryImp(notificationBuilder, notificationManager)
    }

}

