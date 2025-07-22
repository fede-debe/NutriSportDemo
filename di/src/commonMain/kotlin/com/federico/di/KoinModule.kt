package com.federico.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/** We will pass the Android context as config parameter*/
fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
    }
}