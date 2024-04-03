package com.aioapp.nuggetmvp.utils.appextension

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showGenericAlertDialog(message: String) {
    AlertDialog.Builder(this).apply {
        setMessage(message)
        setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}

fun Context.copyFileFromAssets(fileName: String, destinationFile: File) {
    val assetManager = assets
    try {
        assetManager.open(fileName).use { `in` ->
            FileOutputStream(destinationFile).use { out ->
                val buffer = ByteArray(1024)
                var read: Int
                while (`in`.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
            }
        }
    } catch (e: IOException) {
        Log.e("MainActivity", "Error copying file from assets", e)
    }
}

fun Context?.assetsFile(): String? {
    try {
        val file = File.createTempFile("temp", ".ppn")
        val filePath = file.absolutePath
        val inputStream: InputStream? = this?.assets?.open("Hey-Nugget_en_android_v3_0_0.ppn")
        val size = inputStream?.available()
        val buffer = ByteArray(size ?: return "")
        inputStream.read(buffer)
        inputStream.close()
        val out = FileOutputStream(filePath)
        out.write(buffer)
        out.close()
        return filePath
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

@Suppress("DEPRECATION")
fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    val runningServices = activityManager?.getRunningServices(Integer.MAX_VALUE)

    if (runningServices != null) {
        for (service in runningServices) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
    }
    return false
}