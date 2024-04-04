package com.aioapp.nuggetmvp.di.repositories.deepgram

import com.aioapp.nuggetmvp.network.RetrofitApiService
import com.aioapp.nuggetmvp.utils.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class DeepGramRepositoryImp @Inject constructor(private val retrofitApiService: RetrofitApiService) :
    DeepGramRepository {
    override val transcribeAudio: TranscribeAudioFunction =
        { model, smartFormat, language, audio, keywords ->
            flow {
                val response = retryIO {
                    retrofitApiService.transcribeAudio(
                        model = model,
                        smartFormat = smartFormat,
                        language = language,
                        audio = audio, keywords = keywords
                    )
                }
                if (response.isSuccessful) {
                    val restaurantResponse = response.body()
                    emit(
                        com.aioapp.nuggetmvp.utils.Result.Success(
                            restaurantResponse ?: return@flow
                        )
                    )
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
        times: Int = Int.MAX_VALUE, initialDelay: Long = 1000,
        maxDelay: Long = 1000, // 4 seconds
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
