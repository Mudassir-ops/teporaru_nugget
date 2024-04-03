package com.aioapp.nuggetmvp.network

import com.aioapp.nuggetmvp.models.TranscriptionBaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

typealias TranscribeResponse = Response<TranscriptionBaseResponse>

interface RetrofitApiService {
    @POST("v1/listen")
    suspend fun transcribeAudio(
        @Query("model") model: String?,
        @Query("smart_format") smartFormat: Boolean?,
        @Query("language") language: String?,
        @Body audio: RequestBody
    ): TranscribeResponse
}