package com.aioapp.nuggetmvp.di.repositories.deepgram

import com.aioapp.nuggetmvp.models.TranscriptionBaseResponse
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

typealias TranscribeAudioFunction = (
    model: String?, smartFormat: Boolean?, language: String?, audio: RequestBody
) -> Flow<Result<TranscriptionBaseResponse>?>

interface DeepGramRepository {
    val transcribeAudio: TranscribeAudioFunction
}
