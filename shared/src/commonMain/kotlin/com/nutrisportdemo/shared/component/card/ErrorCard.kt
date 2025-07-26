package com.nutrisportdemo.shared.component.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.nutrisportdemo.shared.FontSize

@Composable
fun ErrorCard(
    modifier: Modifier = Modifier,
    message: String,
    fontSize: TextUnit = FontSize.SMALL
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
    }
}
