package com.aioapp.nuggetmvp.repositories

import com.aioapp.nuggetmvp.network.RetrofitApiService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val retrofitApiService: RetrofitApiService) {
}