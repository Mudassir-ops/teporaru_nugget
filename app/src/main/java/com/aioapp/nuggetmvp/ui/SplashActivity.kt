package com.aioapp.nuggetmvp.ui

import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aioapp.nuggetmvp.databinding.ActivitySplashBinding
import com.aioapp.nuggetmvp.service.NuggetCameraService
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.utils.Constants
import com.aioapp.nuggetmvp.utils.appextension.assetsFile
import com.aioapp.nuggetmvp.utils.appextension.isServiceRunning
import com.aioapp.nuggetmvp.utils.enum.ScreenState
import com.aioapp.nuggetmvp.utils.wakeupCallBack


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var currentScreenState: ScreenState = ScreenState.WAKE_UP
    private var binding: ActivitySplashBinding? = null
    private var porcupineManager: PorcupineManager? = null


    private var wakeWordCallback = PorcupineManagerCallback { keywordIndex ->

        Log.e("SplashHiNuggetWakeUp--->", ": $keywordIndex---$currentScreenState")

        if (keywordIndex == 0) {
            if (isServiceRunning(NuggetRecorderService::class.java)) {
                stopService(Intent(this@SplashActivity, NuggetRecorderService::class.java))
                if (isServiceRunning(NuggetCameraService::class.java)) {
                    stopService(Intent(this@SplashActivity, NuggetCameraService::class.java))
                }
                if (ScreenState.WAKE_UP == currentScreenState) {
                    currentScreenState = ScreenState.MAIN_MENU
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    wakeupCallBack?.invoke(true)
                }

            } else {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initPorcupineManager()


        binding?.apply {

        }

        /**
         *@author Mudassir Satti This CallBack will update currentScreen State
         */
//        screenStateUpdateCallback = {
//            currentScreenState = it
//        }
    }

    private fun initPorcupineManager() {
        assetsFile()?.let {
            porcupineManager =
                PorcupineManager.Builder().setAccessKey(Constants.ACCESS_KEY_PORCUPINE)
                    .setKeywordPath(
                        it
                    ).build(this@SplashActivity, wakeWordCallback)
            porcupineManager?.start()
        }
    }

    private fun updateCurrentScreenState(newState: ScreenState) {
        currentScreenState = newState
    }
}