package com.federico.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.federico.home.component.BottomBar
import com.nutrisportdemo.shared.Surface

@Composable
fun HomeGraphScreen() {
    Scaffold(containerColor = Surface) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            Spacer(modifier = Modifier.weight(1f))
            BottomBar(
                selected = false,
                onSelect = { destination ->

                })
        }
    }
}