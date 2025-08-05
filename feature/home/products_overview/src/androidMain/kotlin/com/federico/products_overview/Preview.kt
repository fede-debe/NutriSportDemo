package com.federico.products_overview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.federico.products_overview.component.MainProductCard
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.ProductCategory

@Preview(name = "MainProductCard Variants", showBackground = true)
@Composable
fun MainProductCardPreview(
    @PreviewParameter(MainProductCardPreviewParams::class) param: Boolean
) {
    val product = Product(
        id = "abc123",
        title = "Whey Protein",
        description = "High-quality protein for muscle growth and recovery.",
        thumbnail = "",
        weight = 500,
        price = 29.99,
        category = ProductCategory.Protein.title,
        flavors = null,
    )
    MaterialTheme {
        MainProductCard(
            product = product,
            isLarge = param,
            onClick = {}
        )
    }
}

class MainProductCardPreviewParams : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(
        true, false
    )
}