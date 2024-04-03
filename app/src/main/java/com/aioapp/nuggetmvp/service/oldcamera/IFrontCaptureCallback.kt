package com.aioapp.nuggetmvp.service.oldcamera

interface IFrontCaptureCallback {
    fun onPhotoCaptured(filePath: String?)
    fun onCaptureError(errorCode: Int)
}