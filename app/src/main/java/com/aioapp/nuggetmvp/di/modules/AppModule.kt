package com.aioapp.nuggetmvp.di.modules

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aioapp.nuggetmvp.di.repositories.deepgram.DeepGramRepository
import com.aioapp.nuggetmvp.di.repositories.deepgram.DeepGramRepositoryImp
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepository
import com.aioapp.nuggetmvp.di.repositories.notification.NotificationRepositoryImp
import com.aioapp.nuggetmvp.di.repositories.refill.RefillRepository
import com.aioapp.nuggetmvp.di.repositories.refill.RefillRepositoryImp
import com.aioapp.nuggetmvp.di.repositories.texttoresponse.TextToResponseRepository
import com.aioapp.nuggetmvp.di.repositories.texttoresponse.TextToResponseRepositoryImp
import com.aioapp.nuggetmvp.network.RefillApiService
import com.aioapp.nuggetmvp.network.RetrofitApiService
import com.aioapp.nuggetmvp.network.TextToResponseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat,
    ): NotificationRepository {
        return NotificationRepositoryImp(
            notificationManager = notificationManager, notificationBuilder = notificationBuilder
        )
    }

    @Provides
    @Singleton
    fun provideDeepGramRepository(
        apiService: RetrofitApiService
    ): DeepGramRepository {
        return DeepGramRepositoryImp(retrofitApiService = apiService)
    }

    @Provides
    @Singleton
    fun provideTextToResponseRepository(
        textToResponseApiService: TextToResponseApiService
    ): TextToResponseRepository {
        return TextToResponseRepositoryImp(textToResponseApiService = textToResponseApiService)
    }

    @Provides
    @Singleton
    fun provideRefillRepository(
        refillApiService: RefillApiService
    ): RefillRepository {
        return RefillRepositoryImp(refillApiService = refillApiService)
    }

}

