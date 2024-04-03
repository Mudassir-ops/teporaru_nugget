package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class ModelInfoArch(
    @SerializedName("name") var name: String? = null,
    @SerializedName("version") var version: String? = null,
    @SerializedName("arch") var arch: String? = null
)