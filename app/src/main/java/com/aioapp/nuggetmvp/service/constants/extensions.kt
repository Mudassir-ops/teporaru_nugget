package com.aioapp.nuggetmvp.service.constants

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException


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

fun Context.createFilePathInCacheDirectoryForCamera(fileName: String): String? {
    val sd = cacheDir
    val folder = File(sd, "/myCameraPic/")
    if (!folder.exists()) {
        if (!folder.mkdir()) {
            Log.e("ERROR", "Cannot create a directory!")
        } else {
            folder.mkdirs()
        }
    }
    val outputFile = File(folder, fileName)
    if (!outputFile.exists()) {
        outputFile.createNewFile()
    }
    return outputFile.absolutePath
}

fun Context.createFilePathInCacheDirectoryForCameraDeletedFolder(fileName: String): String? {
    val sd = cacheDir
    val folder = File(sd, "/myCameraPic/")

    // Delete the existing folder if it exists
    if (folder.exists()) {
        val deleted = folder.deleteRecursively()
        if (!deleted) {
            Log.e("ERROR", "Failed to delete existing directory!")
            // Handle the failure case appropriately
        }
    }

    // Create a new folder
    if (!folder.mkdirs()) {
        Log.e("ERROR", "Cannot create a directory!")
        // Handle the failure case appropriately
    }

    val outputFile = File(folder, fileName)

    // Create a new file
    if (!outputFile.exists()) {
        try {
            outputFile.createNewFile()
        } catch (e: IOException) {
            Log.e("ERROR", "Failed to create new file: ${e.message}")
            // Handle the failure case appropriately
        }
    }

    return outputFile.absolutePath
}

fun Fragment?.isFragmentVisible(): Boolean {
    return this@isFragmentVisible != null && this@isFragmentVisible.isAdded && !this@isFragmentVisible.isDetached
}
