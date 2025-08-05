package com.federico.cart

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.federico.cart.component.CartItemCard
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.CartItem
import com.nutrisportdemo.shared.domain.ProductCategory

@Preview(
    name = "CartItemCard Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun CartItemCardPreview() {
    val product = Product(
        id = "1",
        title = "Whey Protein Deluxe",
        description = "Premium protein for muscle recovery.",
        thumbnail = "",
        price = 29.99,
        weight = 750,
        category = ProductCategory.Protein.name,
        flavors = listOf("Vanilla", "Chocolate")
    )

    val cartItem = CartItem(
        id = "cart-1",
        productId = product.id,
        quantity = 2
    )

    CartItemCard(
        product = product,
        cartItem = cartItem,
        onMinusClick = {},
        onPlusClick = {},
        onDeleteClick = {}
    )
}
