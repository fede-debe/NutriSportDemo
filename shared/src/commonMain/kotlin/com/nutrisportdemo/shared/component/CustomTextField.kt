package com.nutrisportdemo.shared.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nutrisportdemo.shared.Alpha
import com.nutrisportdemo.shared.BorderError
import com.nutrisportdemo.shared.BorderIdle
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.SurfaceLighter
import com.nutrisportdemo.shared.TextPrimary

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    expanded: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
) {
    val borderColor by animateColorAsState(
        targetValue = if (isError) BorderError else BorderIdle
    )

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary.copy(alpha = Alpha.DISABLED),
        disabledTextColor = TextPrimary.copy(alpha = Alpha.DISABLED),

        focusedContainerColor = SurfaceLighter,
        unfocusedContainerColor = SurfaceLighter,
        disabledContainerColor = SurfaceLighter,
        errorContainerColor = SurfaceLighter,

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth()
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(6.dp)),
        enabled = enabled,
        placeholder =
            if (placeholder != null) {
                {
                    Text(
                        text = placeholder,
                        modifier = Modifier.alpha(Alpha.DISABLED),
                        fontSize = FontSize.REGULAR
                    )
                }
            } else null,
        singleLine = !expanded,
        shape = RoundedCornerShape(size = 6.dp),
        keyboardOptions = keyboardOptions,
        colors = textFieldColors
    )
}