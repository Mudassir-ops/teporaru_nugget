package com.aioapp.nuggetmvp.models

import com.google.gson.annotations.SerializedName


data class MetadataEntity(
    @SerializedName("transaction_key") var transactionKey: String? = null,
    @SerializedName("request_id") var requestId: String? = null,
    @SerializedName("sha256") var sha256: String? = null,
    @SerializedName("created") var created: String? = null,
    @SerializedName("duration") var duration: Double? = null,
    @SerializedName("channels") var channels: Int? = null,
    @SerializedName("models") var models: ArrayList<String> = arrayListOf(),
    @SerializedName("model_info") var modelInfoEntity: ModelInfoEntity? = ModelInfoEntity()
)