package com.aioapp.nuggetmvp.service.camerapicture

object Constants {
    const val PREFERENCE_IS_PHOTO_CAPTURED = "Pref_Is_Photo_Captured"
    const val PREFERENCE_PHOTO_PATH = "pref_photo_path"
    const val LOG_TAG = "GetBack"
    const val DEBUG_FLAG = false
    var listener: ((String?) -> Unit)? = null
}