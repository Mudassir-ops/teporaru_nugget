package com.aioapp.nuggetmvp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParametersEntity(
    @SerializedName("name") var name: String? = null,
    @SerializedName("quantity") var quantity: Int? = 1,
    @SerializedName("menu_type") var menuType: String? = null,
    @SerializedName("required_thing") var requiredThing: String? = null,
    @SerializedName("level") var level: String? = null,
    @SerializedName("message") var message: String? = null
) : Parcelable