package com.alwaystinkering.sandbot

import android.app.Application
import com.alwaystinkering.sandbot.di.DaggerAppComponent

class App : Application() {

    // Create app component with application context to provide to dagger
    val appComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}