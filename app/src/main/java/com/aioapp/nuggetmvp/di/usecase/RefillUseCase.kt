package com.aioapp.nuggetmvp.di.usecase


import com.aioapp.nuggetmvp.di.repositories.refill.RefillRepository
import com.aioapp.nuggetmvp.models.RefillResponseEntity
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class RefillUseCase @Inject constructor(private val refillRepository: RefillRepository) {
    fun invokeTRefillUseCase(
        image: MultipartBody.Part
    ): Flow<Result<RefillResponseEntity>?> {
        return refillRepository.refill(
            image
        )
    }
}