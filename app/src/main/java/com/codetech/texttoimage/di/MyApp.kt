package com.codetech.texttoimage.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}