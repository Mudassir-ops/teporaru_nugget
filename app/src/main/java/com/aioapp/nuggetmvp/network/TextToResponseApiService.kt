package com.aioapp.nuggetmvp.network

import com.aioapp.nuggetmvp.models.TextToResponseEntity
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming


typealias TextToResponse = Response<TextToResponseEntity>

interface TextToResponseApiService {

    @POST("text_to_response/")
    suspend fun textToResponse(
        @Body body: TextToResponseRequestBody
    ): TextToResponse

    @POST("text_to_response_stream/")
    @Streaming
    suspend fun getStreamingResponse(
        @Body body: TextToResponseRequestBody
    ): ResponseBody
}