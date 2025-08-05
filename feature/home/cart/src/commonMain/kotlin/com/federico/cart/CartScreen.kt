package com.federico.cart

import ContentWithMessageBar
import MessageBarState
import androidx.compose.runtime.Composable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.federico.cart.component.CartItemCard
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.SurfaceBrand
import com.nutrisportdemo.shared.SurfaceError
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.TextWhite
import com.nutrisportdemo.shared.component.card.InfoCard
import com.nutrisportdemo.shared.component.card.LoadingCard
import com.nutrisportdemo.shared.domain.CartItem
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.util.DisplayResult
import com.nutrisportdemo.shared.util.RequestState
import rememberMessageBarState

@Composable
fun CartScreen() {
    val viewModel = koinViewModel<CartViewModel>()
    val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsState(RequestState.Loading)
    val messageBarState = rememberMessageBarState()

    CartScreen(
        cartItemsWithProducts = cartItemsWithProducts,
        messageBarState = messageBarState,
        onUpdateCartQuantity = { id, quantity ->
            viewModel.updateCartItemQuantity(
                id = id,
                quantity = quantity,
                onSuccess = {},
                onError = { message -> messageBarState.addError(message) }
            )
        },
        onDeleteCartItem = { id ->
            viewModel.deleteCartItem(
                id = id,
                onSuccess = {},
                onError = { message -> messageBarState.addError(message) }
            )
        }
    )
}

@Composable
fun CartScreen(
    cartItemsWithProducts: RequestState<List<Pair<CartItem, Product>>>,
    messageBarState: MessageBarState,
    onUpdateCartQuantity: (String, Int) -> Unit,
    onDeleteCartItem: (String) -> Unit
) {

    ContentWithMessageBar(
        contentBackgroundColor = Surface,
        messageBarState = messageBarState,
        errorMaxLines = 2,
        errorContainerColor = SurfaceError,
        errorContentColor = TextWhite,
        successContainerColor = SurfaceBrand,
        successContentColor = TextPrimary
    ) {
        cartItemsWithProducts.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { data ->
                if (data.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = data,
                            key = { it.first.id }
                        ) { pair ->
                            CartItemCard(
                                cartItem = pair.first,
                                product = pair.second,
                                onMinusClick = { quantity ->
                                    onUpdateCartQuantity(
                                        pair.first.id,
                                        quantity
                                    )
                                },
                                onPlusClick = { quantity ->
                                    onUpdateCartQuantity(
                                        pair.first.id,
                                        quantity
                                    )
                                },
                                onDeleteClick = { onDeleteCartItem(pair.first.id) }
                            )
                        }
                    }
                } else {
                    InfoCard(
                        image = Resources.Image.ShoppingCart,
                        title = "Empty Cart",
                        subtitle = "Check some of our products."
                    )
                }
            },
            onError = { message ->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            },
            // default animation is not convenient with this case
            transitionSpec = fadeIn() togetherWith fadeOut()
        )
    }
}