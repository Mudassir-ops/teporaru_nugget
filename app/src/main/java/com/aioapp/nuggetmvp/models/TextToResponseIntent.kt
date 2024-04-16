package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName

data class TextToResponseIntent(
    @SerializedName("intent") var intent: String? = null,
    @SerializedName("parameters") var parametersEntity: ParametersEntity? = null,
    @SerializedName("last") var last: Boolean? = false,
    @SerializedName("time") var time: String? = null
)
