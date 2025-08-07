package com.federico.category_search

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.util.RequestState

@Preview(showBackground = true)
@Composable
fun CategorySearchScreenPreview(
    @PreviewParameter(RequestStatePreviewProvider::class)
    state: RequestState<List<Product>>
) {
    MaterialTheme {
        CategorySearchScreen(
            category = ProductCategory.Protein,
            searchQuery = "",
            filteredProducts = state,
            onUpdateSearchQuery = {},
            navigateToDetails = {},
            navigateBack = {}
        )
    }
}

class RequestStatePreviewProvider : PreviewParameterProvider<RequestState<List<Product>>> {
    override val values: Sequence<RequestState<List<Product>>> = sequenceOf(
        RequestState.Idle,
        RequestState.Loading,
        RequestState.Success(
            listOf(
                Product(
                    id = "1",
                    title = "Whey Protein",
                    description = "Delicious and effective protein for muscle growth.",
                    thumbnail = "https://via.placeholder.com/150",
                    category = ProductCategory.Protein.name,
                    price = 29.99,
                    weight = 1000
                ),
                Product(
                    id = "2",
                    title = "Creatine Monohydrate",
                    description = "Increase strength and performance with pure creatine.",
                    thumbnail = "https://via.placeholder.com/150",
                    category = ProductCategory.Creatine.name,
                    price = 19.99,
                    weight = 300
                )
            )
        ),
        RequestState.Error("Something went wrong...")
    )
}


