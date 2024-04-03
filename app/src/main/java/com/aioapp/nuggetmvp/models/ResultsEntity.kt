package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class ResultsEntity(
    @SerializedName("channels") var channels: ArrayList<ChannelsEntity> = arrayListOf()
)