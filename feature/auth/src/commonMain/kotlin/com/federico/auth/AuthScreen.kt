package com.federico.auth

import ContentWithMessageBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.federico.auth.component.GoogleButton
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.nutrisportdemo.shared.Alpha
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.TextSecondary
import com.nutrisportdemo.shared.bebasNeueFont
import rememberMessageBarState

@Composable
fun AuthScreen() {
    val messageBarState = rememberMessageBarState()
    var loadingState by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
            messageBarState = messageBarState,
            errorMaxLines = 2
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(all = 24.dp)) {
                // take full space of the screen weight = 1f
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NUTRISPORT",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = bebasNeueFont(),
                        fontSize = FontSize.EXTRA_LARGE,
                        color = TextSecondary
                    )

                    Text(
                        text = "Sign in to continue",
                        modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.EXTRA_REGULAR,
                        color = TextPrimary
                    )

                }

                GoogleButtonUiContainerFirebase(linkAccount = false, onResult = { result ->
                    result.onSuccess { user ->
                        messageBarState.addSuccess("Authentication successful!")
                        loadingState = false
                    }.onFailure { error ->
                        if (error.message?.contains("A network error") == true) {
                            messageBarState.addError("Internet connection unavailable")
                        } else if (error.message?.contains("Idtoken is null") == true) {
                            messageBarState.addError("Sign in canceled")
                        } else {
                            messageBarState.addError(message = error.message ?: "Unknow")
                        }
                        loadingState = false
                    }
                }) {
                    GoogleButton(
                        loading = loadingState,
                        onClick = {
                            loadingState = true
                            this@GoogleButtonUiContainerFirebase.onClick()
                        }
                    )
                }
            }
        }
    }
}