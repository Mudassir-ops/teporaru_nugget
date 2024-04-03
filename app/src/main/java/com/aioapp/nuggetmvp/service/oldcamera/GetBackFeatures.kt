package com.aioapp.nuggetmvp.service.oldcamera

import java.io.Serializable

class GetBackFeatures : Serializable {
    private var isPhotoCapture: Boolean
    private var isLocation: Boolean
    private var isClearContacts: Boolean
    private var isClearSms: Boolean
    private var isFormatSdCard: Boolean
    private var isClearEmailAccounts = false

    init {
        isFormatSdCard = isClearEmailAccounts
        isClearSms = isFormatSdCard
        isClearContacts = isClearSms
        isLocation = isClearContacts
        isPhotoCapture = isLocation
    }

    override fun toString(): String {
        return ("photoCapture = " + isPhotoCapture + ", location = " + isLocation
                + ", clearContacts = " + isClearContacts + ", clearSms = "
                + isClearSms + ", formatSdCard = " + isFormatSdCard
                + ", clearEmailAccounts = " + isClearEmailAccounts)
    }

    companion object {
        private const val serialVersionUID = 3969543635768771371L
    }
}