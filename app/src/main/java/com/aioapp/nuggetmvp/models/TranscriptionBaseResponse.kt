package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class TranscriptionBaseResponse(
    @SerializedName("metadata") var metadata: MetadataEntity? = MetadataEntity(),
    @SerializedName("results") var resultsEntity: ResultsEntity? = ResultsEntity()
)