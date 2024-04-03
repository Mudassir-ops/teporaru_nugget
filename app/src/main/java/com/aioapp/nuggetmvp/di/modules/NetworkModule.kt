package com.aioapp.nuggetmvp.di.modules

import android.content.Context
import com.aioapp.nuggetmvp.network.NetworkConnectionInterceptor
import com.aioapp.nuggetmvp.network.RefillApiService
import com.aioapp.nuggetmvp.network.RequestInterceptorForDeepGram
import com.aioapp.nuggetmvp.network.RetrofitApiService
import com.aioapp.nuggetmvp.network.TextToResponseApiService
import com.aioapp.nuggetmvp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    fun provideDeepGramClientService(retrofitBuilder: Retrofit.Builder): RetrofitApiService {
        val baseUrl = Constants.BASE_URL
        return retrofitBuilder.baseUrl(baseUrl).build().create(RetrofitApiService::class.java)
    }

    @Provides
    fun provideTextToResponseService(retrofitBuilder: Retrofit.Builder): TextToResponseApiService {
        val baseUrl = Constants.BASE_URL_TEXT_TO_RESPONSE
        return retrofitBuilder.baseUrl(baseUrl).build().create(TextToResponseApiService::class.java)
    }

    @Provides
    fun provideRefillService(retrofitBuilder: Retrofit.Builder): RefillApiService {
        val baseUrl = Constants.BASE_URL_Refill
        return retrofitBuilder.baseUrl(baseUrl).build().create(RefillApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        @ApplicationContext context: Context, interceptor: RequestInterceptorForDeepGram
    ): OkHttpClient {
        return OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(NetworkConnectionInterceptor(context)).addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    fun provideRequestInterceptor(): RequestInterceptorForDeepGram {
        return RequestInterceptorForDeepGram()
    }

}