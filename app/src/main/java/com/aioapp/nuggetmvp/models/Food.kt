package com.aioapp.nuggetmvp.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Food(
    var image: Int,
    var fullImg: Int,
    var name: String? = "",
    var price: String? = ""
) : Parcelable
