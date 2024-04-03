package com.aioapp.nuggetmvp.network

import com.aioapp.nuggetmvp.models.RefillResponseEntity
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


typealias RefillResponse = Response<RefillResponseEntity>

interface RefillApiService {
    @Multipart
    @POST("predict/")
    suspend fun refill(
        @Part image: MultipartBody.Part
    ): RefillResponse
}