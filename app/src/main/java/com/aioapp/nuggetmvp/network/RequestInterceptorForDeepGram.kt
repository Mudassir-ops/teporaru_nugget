package com.aioapp.nuggetmvp.network

import com.aioapp.nuggetmvp.utils.Constants.DEEP_GRAM_ACCESS_TOKEN
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptorForDeepGram : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Content-Type", "audio/wave")
            .addHeader("Accept", "application/json")
            .addHeader(
                "Authorization",
                DEEP_GRAM_ACCESS_TOKEN
            )
            .build()
        return chain.proceed(newRequest)
    }
}