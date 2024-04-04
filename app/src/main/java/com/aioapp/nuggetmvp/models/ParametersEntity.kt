package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class ParametersEntity(
    @SerializedName("name") var name: String? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("menu_type") var menuType: String? = null,
    @SerializedName("required_thing") var requiredThing: String? = null
)