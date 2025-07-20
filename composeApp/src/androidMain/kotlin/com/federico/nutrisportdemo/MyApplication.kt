package com.federico.nutrisportdemo

import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(context = this)
    }
}