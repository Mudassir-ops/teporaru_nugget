package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class ChannelsEntity(
    @SerializedName("alternatives") var alternatives: ArrayList<AlternativesEntity> = arrayListOf()
)