package com.aioapp.nuggetmvp.models


import android.os.Parcel
import android.os.Parcelable


data class Food(
    var image: Int,
    var fullImg: Int,
    var logicalName: String? = "",
    var price: String? = "",
    var displayName: String? = "",
    var itemQuantity: Int = 1,
    var count: Int = 1
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(image)
        parcel.writeInt(fullImg)
        parcel.writeString(logicalName)
        parcel.writeString(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }

}
