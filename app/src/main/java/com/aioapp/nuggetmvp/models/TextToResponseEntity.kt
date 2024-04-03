package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName

data class TextToResponseEntity(
    @SerializedName("intents") var intents: ArrayList<TextToResponseIntent>? = null,
)