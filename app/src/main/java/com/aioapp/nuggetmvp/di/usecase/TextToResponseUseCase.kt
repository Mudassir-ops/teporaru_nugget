package com.aioapp.nuggetmvp.di.usecase


import com.aioapp.nuggetmvp.di.repositories.texttoresponse.TextToResponseRepository
import com.aioapp.nuggetmvp.models.TextToResponseEntity
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TextToResponseUseCase @Inject constructor(private val textToResponseRepository: TextToResponseRepository) {
    fun invokeTextToResponseUseCase(
        keywords: List<String>,
        body: TextToResponseRequestBody
    ): Flow<Result<TextToResponseEntity>?> {
        return textToResponseRepository.textToResponse(
            keywords, body
        )
    }
}