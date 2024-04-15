package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName

data class TextToResponseRequestBody(
    @SerializedName("user_prompt") var userPrompt: String? = null,
    @SerializedName("order") var orderEntity: ArrayList<OrderEntity>? = null,
    @SerializedName("screen_state") var screenState: Int? = null,
    @SerializedName("order_status") var orderStatus: Int? = null,
)