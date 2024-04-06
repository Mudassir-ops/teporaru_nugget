package com.aioapp.nuggetmvp

import android.app.Application
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferenceUtil.init(this@MyApplication)
    }
}