package com.aioapp.nuggetmvp.di.usecase


import com.aioapp.nuggetmvp.di.repositories.deepgram.DeepGramRepository
import com.aioapp.nuggetmvp.models.TranscriptionBaseResponse
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import javax.inject.Inject

class TranscribeAudioWithDeepGramUseCase @Inject constructor(private val deepGramRepository: DeepGramRepository) {

    fun invokeTranscribeAudioUseCase(
        model: String?,
        smartFormat: Boolean?,
        language: String?,
        audio: RequestBody,
        keywords: List<String>
    ): Flow<Result<TranscriptionBaseResponse>?> {
        return deepGramRepository.transcribeAudio(
            model, smartFormat, language, audio, keywords
        )
    }
}