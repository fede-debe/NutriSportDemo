package com.federico.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.federico.profile.component.AlertTextField
import com.federico.profile.component.CustomTextField
import com.nutrisportdemo.shared.FontSize

@Preview(showBackground = true)
@Composable
fun ProfileModulePreviews() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "CustomTextField",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.SemiBold
                )

                var text by remember { mutableStateOf("") }
                CustomTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Enter your name",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "AlertTextField",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.SemiBold
                )

                AlertTextField(text = "text", onClick = {})
            }
        }
    }
}