package com.federico.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.util.RequestState
import rememberMessageBarState

@Preview(
    name = "DetailsScreen Product Variants",
    showBackground = true,
    backgroundColor = 0xFFF2F2F2
)
@Composable
fun DetailsScreenProductVariantsPreview(
    @PreviewParameter(ProductPreviewProvider::class) product: Product
) {
    DetailsScreen(
        messageBarState = rememberMessageBarState(),
        product = RequestState.Success(product),
        selectedFlavor = product.flavors?.firstOrNull(),
        quantity = 1,
        onUpdateQuantity = {},
        onUpdateFlavor = {},
        onAddItemToCart = {},
        navigateBack = {}
    )
}

class ProductPreviewProvider : PreviewParameterProvider<Product> {
    override val values = sequenceOf(
        Product(
            id = "1",
            title = "Whey Protein Deluxe",
            description = "High-quality protein powder with added BCAAs.",
            thumbnail = "",
            price = 29.99,
            weight = 750,
            category = ProductCategory.Protein.name,
            flavors = listOf("Vanilla", "Chocolate", "Strawberry")
        ),
        Product(
            id = "2",
            title = "Fitness Shaker Bottle",
            description = "Durable BPA-free shaker bottle with a mixing ball.",
            thumbnail = "",
            price = 9.99,
            weight = 0,
            category = ProductCategory.Accessories.name,
            flavors = null
        )
    )
}
