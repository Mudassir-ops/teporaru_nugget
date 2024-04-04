package com.aioapp.nuggetmvp.di.repositories.menu

import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow


interface MenuRepository {
    fun allMenuData(): Flow<Result<List<Food>>>
}