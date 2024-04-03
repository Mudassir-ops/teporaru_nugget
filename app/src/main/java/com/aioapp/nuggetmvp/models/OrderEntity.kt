package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName

data class OrderEntity(
    @SerializedName("name") var name: String? = null,
    @SerializedName("quantity") var quantity: Int? = null
)