package com.aioapp.nuggetmvp.di.repositories.refill

import com.aioapp.nuggetmvp.models.RefillResponseEntity
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

typealias Refill = (
    image: MultipartBody.Part
) -> Flow<Result<RefillResponseEntity>?>

interface RefillRepository {
    val refill: Refill
}
