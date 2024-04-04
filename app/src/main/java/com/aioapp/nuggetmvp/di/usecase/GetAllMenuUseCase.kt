package com.aioapp.nuggetmvp.di.usecase


import com.aioapp.nuggetmvp.di.repositories.menu.MenuRepository
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMenuUseCase @Inject constructor(private val menuRepository: MenuRepository) {

    fun invokeAllMenuDataUseCase(): Flow<Result<List<Food>>> {
        return menuRepository.allMenuData()
    }

}