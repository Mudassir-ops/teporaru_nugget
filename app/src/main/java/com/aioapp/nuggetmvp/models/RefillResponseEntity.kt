package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName

data class RefillResponseEntity(
    @SerializedName("prediction") val prediction: String
)