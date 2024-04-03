package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class WordsEntity(
    @SerializedName("word") var word: String? = null,
    @SerializedName("start") var start: Double? = null,
    @SerializedName("end") var end: Double? = null,
    @SerializedName("confidence") var confidence: Double? = null,
    @SerializedName("punctuated_word") var punctuatedWord: String? = null
)