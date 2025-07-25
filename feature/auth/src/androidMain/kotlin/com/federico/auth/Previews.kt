package com.federico.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.federico.auth.component.GoogleButton

class LoadingStateProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

@Preview(showBackground = true)
@Composable
fun GoogleButtonPreview(
    @PreviewParameter(LoadingStateProvider::class) isLoading: Boolean
) {
    GoogleButton(
        modifier = Modifier.padding(16.dp),
        loading = isLoading,
        onClick = {}
    )
}