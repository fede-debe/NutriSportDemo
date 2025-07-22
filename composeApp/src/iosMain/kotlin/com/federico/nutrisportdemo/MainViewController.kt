package com.federico.nutrisportdemo

import androidx.compose.ui.window.ComposeUIViewController
import com.federico.di.initializeKoin

/** configure parameter will be triggered whenever the iOS application starts, and this is why
 * we are initializing Koin here. */
fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }