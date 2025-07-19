package com.federico.nutrisportdemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.federico.navigation.SetupNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
//
//        var appReady by remember { mutableStateOf(false) }
//
//        LaunchedEffect(Unit) {
//            GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = Constants.WEB_CLIENT_ID))
//            appReady = true
//        }

//        AnimatedVisibility(modifier = Modifier.fillMaxSize(), visible = appReady) {
            SetupNavigation()
//        }
    }
}