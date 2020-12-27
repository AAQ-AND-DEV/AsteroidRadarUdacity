package com.udacity.asteroidradar

import android.app.Application
import timber.log.Timber

class AsteroidApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}