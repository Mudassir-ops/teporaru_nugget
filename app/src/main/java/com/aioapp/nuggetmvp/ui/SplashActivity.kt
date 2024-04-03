package com.aioapp.nuggetmvp.ui

import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aioapp.nuggetmvp.databinding.ActivitySplashBinding
import com.aioapp.nuggetmvp.utils.Constants
import com.aioapp.nuggetmvp.utils.enum.ScreenState
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var currentScreenState: ScreenState = ScreenState.WAKE_UP
    private var binding: ActivitySplashBinding? = null
    private var porcupineManager: PorcupineManager? = null
    private var wakeWordCallback = PorcupineManagerCallback { keywordIndex ->
        if (keywordIndex == 0) {
            if (ScreenState.WAKE_UP == currentScreenState) {
                currentScreenState = ScreenState.MAIN_MENU
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            } else {
                wakeupCallBack?.invoke(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initPorcupineManager()
        /**
         *@author Mudassir Satti This CallBack will update currentScreen State
         */
//        screenStateUpdateCallback = {
//            currentScreenState = it
//        }
    }

    private fun initPorcupineManager() {
        val datasetTempFile = getAssestFIle()
        Log.e("asdsadass", "initPorcupineManager: $datasetTempFile ")
        porcupineManager =
            PorcupineManager.Builder().setAccessKey(Constants.ACCESS_KEY_PORCUPINE).setKeywordPath(
                datasetTempFile
            ).build(this@SplashActivity, wakeWordCallback)
        porcupineManager?.start()
    }

    private fun getAssestFIle(): String {
        try {
            val file = File.createTempFile("temp", ".ppn")
            val filePath = file.absolutePath
            val inputStream: InputStream = assets.open("Hey-Nugget_en_android_v3_0_0.ppn")
            val size = inputStream.available()
            val buffer = ByteArray(size)
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

    private fun updateCurrentScreenState(newState: ScreenState) {
        currentScreenState = newState
    }
}