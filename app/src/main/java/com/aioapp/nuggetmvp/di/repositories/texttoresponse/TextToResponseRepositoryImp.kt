package com.aioapp.nuggetmvp.di.repositories.texttoresponse

import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.network.TextToResponseApiService
import com.aioapp.nuggetmvp.utils.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.io.IOException
import javax.inject.Inject

class TextToResponseRepositoryImp @Inject constructor(private val textToResponseApiService: TextToResponseApiService) :
    TextToResponseRepository {

    override fun getStreamingResponse(body: TextToResponseRequestBody): Flow<String> = flow {
        try {
            val responseBody: ResponseBody = textToResponseApiService.getStreamingResponse(body)
            responseBody.byteStream().bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    emit(line)
                }
            }
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }

    override val textToResponse: TextToResponse = { body ->
        flow {
            val response = retryIO {
                textToResponseApiService.textToResponse(
                    body = body
                )
            }
            if (response.isSuccessful) {
                val restaurantResponse = response.body()
                emit(com.aioapp.nuggetmvp.utils.Result.Success(restaurantResponse ?: return@flow))
            } else {
                emit(
                    com.aioapp.nuggetmvp.utils.Result.Error(
                        ApiException(response.code(), response.message())
                    )
                )
            }
        }
    }

    private suspend fun <T : Any> retryIO(
        times: Int = Int.MAX_VALUE, initialDelay: Long = 1000, maxDelay: Long = 1000, // 4 seconds
        factor: Double = 2.0, block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: IOException) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
        return block()
    }
}
