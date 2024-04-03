package com.aioapp.nuggetmvp.di.repositories.texttoresponse

import com.aioapp.nuggetmvp.models.TextToResponseEntity
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow

typealias TextToResponse = (
    body: TextToResponseRequestBody
) -> Flow<Result<TextToResponseEntity>?>

interface TextToResponseRepository {
    val textToResponse: TextToResponse
}
