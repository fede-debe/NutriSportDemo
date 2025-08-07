package com.federico.manage_product

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.util.RequestState
import rememberMessageBarState

@Preview(
    name = "ManageProductScreen Variants",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun ManageProductScreenPreview(
    @PreviewParameter(ManageProductScreenPreviewProvider::class)
    previewData: ManageProductPreviewData
) {
    MaterialTheme {
        ManageProductScreen(
            id = previewData.id,
            screenState = previewData.state,
            messageBarState = rememberMessageBarState(),
            thumbnailUploaderState = previewData.thumbnailUploaderState,
            isFormValid = previewData.isFormValid,
            onOpenPhotoPicker = {},
            onDeleteProduct = {},
            onDeleteThumbnailFromStorage = {},
            onUpdateThumbnailUploaderState = {},
            onUpdateTitle = {},
            onUpdateDescription = {},
            onUpdateWeight = {},
            onUpdateFlavors = {},
            onUpdatePrice = {},
            onUpdateIsNew = {},
            onUpdateIsPopular = {},
            onUpdateIsDiscounted = {},
            onToggleCategoryDialog = {},
            onSubmitProduct = {},
            navigateBack = {}
        )
    }
}

data class ManageProductPreviewData(
    val id: String?,
    val state: ManageProductState,
    val isFormValid: Boolean,
    val thumbnailUploaderState: RequestState<Unit>
)

class ManageProductScreenPreviewProvider : PreviewParameterProvider<ManageProductPreviewData> {
    override val values: Sequence<ManageProductPreviewData> = sequenceOf(
        ManageProductPreviewData(
            id = null,
            state = ManageProductState(
                title = "",
                description = "",
                thumbnail = "",
                category = ProductCategory.Protein,
                weight = null,
                flavors = "",
                price = 0.0,
                isNew = false,
                isPopular = false,
                isDiscounted = false
            ),
            isFormValid = false,
            thumbnailUploaderState = RequestState.Idle
        ),

        ManageProductPreviewData(
            id = "product-123",
            state = ManageProductState(
                title = "Whey Protein",
                description = "High-quality whey protein isolate.",
                thumbnail = "",
                category = ProductCategory.Protein,
                weight = 500,
                flavors = "Vanilla, Chocolate",
                price = 29.99,
                isNew = true,
                isPopular = true,
                isDiscounted = false
            ),
            isFormValid = true,
            thumbnailUploaderState = RequestState.Success(Unit)
        ),

        ManageProductPreviewData(
            id = "product-123",
            state = ManageProductState(
                title = "Protein Bar",
                description = "Delicious high-protein snack.",
                thumbnail = "",
                category = ProductCategory.Protein,
                weight = 100,
                flavors = "Peanut Butter",
                price = 1.99,
                isNew = false,
                isPopular = true,
                isDiscounted = true
            ),
            isFormValid = true,
            thumbnailUploaderState = RequestState.Loading
        ),

        ManageProductPreviewData(
            id = "product-123",
            state = ManageProductState(
                title = "BCAA Supplement",
                description = "Boost recovery with BCAAs.",
                thumbnail = "",
                category = ProductCategory.Accessories,
                weight = null,
                flavors = "",
                price = 15.49,
                isNew = false,
                isPopular = false,
                isDiscounted = false
            ),
            isFormValid = true,
            thumbnailUploaderState = RequestState.Error("Failed to upload thumbnail")
        )
    )
}

