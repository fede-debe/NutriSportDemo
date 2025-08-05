package com.federico.categories

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
import com.federico.categories.component.CategoryCard
import com.nutrisportdemo.shared.domain.ProductCategory

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview(
    @PreviewParameter(CategoryPreviewProvider::class) category: ProductCategory
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            CategoryCard(
                category = category,
                onClick = {}
            )
        }
    }
}


class CategoryPreviewProvider : PreviewParameterProvider<ProductCategory> {
    override val values = sequenceOf(
        ProductCategory.Protein,
        ProductCategory.Creatine,
        ProductCategory.PreWorkout,
        ProductCategory.Gainers,
        ProductCategory.Accessories
    )
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    MaterialTheme {
        CategoriesScreen(navigateToCategorySearch = {})
    }
}
