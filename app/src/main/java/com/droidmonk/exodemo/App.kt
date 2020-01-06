package com.droidmonk.exodemo

import android.app.Application

class App: Application() {
    lateinit var appContainer:AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer=AppContainer(this)
    }
}