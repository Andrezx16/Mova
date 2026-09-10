package com.uilover.project304

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LuxeRealtyApp : Application() {
    companion object {
        lateinit var instance: LuxeRealtyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
