package com.aioapp.nuggetmvp.utils.all_extension

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
