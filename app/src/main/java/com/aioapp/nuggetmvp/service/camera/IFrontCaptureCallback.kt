package com.aioapp.nuggetmvp.service.camera

interface IFrontCaptureCallback {
    fun onPhotoCaptured(filePath: String?)
}