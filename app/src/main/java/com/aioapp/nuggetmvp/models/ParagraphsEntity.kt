package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class ParagraphsEntity(
    @SerializedName("transcript") var transcript: String? = null,
    @SerializedName("paragraphs") var paragraphs: ArrayList<ParagraphsEntity> = arrayListOf()
)