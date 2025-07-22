package com.federico.nutrisportdemo

import android.app.Application
import com.federico.di.initializeKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            config = {
                androidContext(this@MyApplication)
            }
        )
        Firebase.initialize(context = this)
    }
}