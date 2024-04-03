package com.aioapp.nuggetmvp.service.camerapicture

interface IFrontCaptureCallback {
    fun onPhotoCaptured(filePath: String?)
    fun onCaptureError(errorCode: Int)
}