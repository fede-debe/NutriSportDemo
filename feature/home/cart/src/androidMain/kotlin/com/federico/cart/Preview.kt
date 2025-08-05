package com.federico.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.federico.cart.component.CartItemCard
import com.nutrisportdemo.shared.domain.CartItem
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.util.RequestState
import rememberMessageBarState

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

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            CartItemCard(
                product = product,
                cartItem = cartItem,
                onMinusClick = {},
                onPlusClick = {},
                onDeleteClick = {}
            )
        }
    }
}

class CartItemsPreviewParams :
    PreviewParameterProvider<RequestState<List<Pair<CartItem, Product>>>> {
    override val values: Sequence<RequestState<List<Pair<CartItem, Product>>>> = sequenceOf(
        RequestState.Success(
            listOf(
                CartItem(
                    productId = "p1",
                    flavor = "Vanilla",
                    quantity = 2
                ) to Product(
                    id = "p1",
                    title = "Whey Protein",
                    description = "High-quality protein.",
                    thumbnail = "https://joooinn.com/images/dog-67.jpg",
                    category = "Proteins",
                    weight = 1000,
                    price = 39.99
                ),
                CartItem(
                    productId = "p2",
                    flavor = null,
                    quantity = 1
                ) to Product(
                    id = "p2",
                    title = "Shaker Bottle",
                    description = "Leak-proof shaker.",
                    thumbnail = "https://joooinn.com/images/dog-67.jpg",
                    category = "Accessories",
                    price = 9.99
                )
            )
        ),
        RequestState.Success(listOf()),
        RequestState.Error("Something went wrong while loading the cart.")
    )
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview(
    @PreviewParameter(CartItemsPreviewParams::class) cartItemsWithProducts: RequestState<List<Pair<CartItem, Product>>>
) {
    MaterialTheme {
        CartScreen(
            cartItemsWithProducts = cartItemsWithProducts,
            messageBarState = rememberMessageBarState(),
            onUpdateCartQuantity = { _, _ -> },
            onDeleteCartItem = {}
        )
    }
}
