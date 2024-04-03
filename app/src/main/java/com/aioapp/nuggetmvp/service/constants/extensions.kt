package com.aioapp.nuggetmvp.service.constants

import android.content.Context
import android.util.Log
import java.io.File


fun Context.createFilePathInCacheDirectory(): String? {
    val sd = cacheDir
    val folder = File(sd, "/myVoices/")
    if (!folder.exists()) {
        if (!folder.mkdir()) {
            Log.e("ERROR", "Cannot create a directory!")
        } else {
            folder.mkdirs()
        }
    }
    val fileName = "audio_file.m4a"
    val outputFile = File(folder, fileName)
    if (!outputFile.exists()) {
        outputFile.createNewFile()
    }
    return outputFile.absolutePath
}
