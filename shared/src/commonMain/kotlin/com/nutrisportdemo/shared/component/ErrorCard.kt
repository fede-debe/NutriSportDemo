package com.nutrisportdemo.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.nutrisportdemo.shared.FontSize

@Composable
fun ErrorCard(
    modifier: Modifier = Modifier,
    message: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            fontSize = FontSize.SMALL,
            textAlign = TextAlign.Center
        )
    }
}
