package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class AlternativesEntity(
    @SerializedName("transcript") var transcript: String? = null,
    @SerializedName("confidence") var confidence: Double? = null,
    @SerializedName("words") var words: ArrayList<WordsEntity> = arrayListOf(),
    @SerializedName("paragraphs") var paragraphsEntity: ParagraphsEntity? = ParagraphsEntity()
)