package com.federico.manage_product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.federico.data.domain.AdminRepository
import com.federico.manage_product.util.toProduct
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ManageProductState @OptIn(
    ExperimentalUuidApi::class,
    ExperimentalTime::class
) constructor(
    val id: String = Uuid.random().toHexString(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "thumbnail image",
    val category: ProductCategory = ProductCategory.Protein,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false,
    val isNew: Boolean = false
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId = savedStateHandle.get<String>("productId") ?: ""
    var screenState by mutableStateOf(ManageProductState())
        private set

    var thumbnailUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)
        private set
    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0

    init {
        viewModelScope.launch {
            productId.takeIf { it.isNotEmpty() }?.let { productId ->
                val selectedProduct = adminRepository.readProductById(productId)
                if (selectedProduct.isSuccess()) {
                    val product = selectedProduct.getSuccessData()

                    product.apply {
                        updateId(id)
                        updateCreatedAt(createdAt)
                        updateTitle(title)
                        updateDescription(description)
                        updateThumbnail(thumbnail)
                        updateThumbnailUploaderState(RequestState.Success(Unit))
                        updateCategory(ProductCategory.valueOf(category))
                        updateFlavors(flavors?.joinToString(",") ?: "")
                        updateWeight(weight)
                        updatePrice(price)
                        updateIsPopular(isPopular)
                        updateIsDiscounted(isDiscounted)
                        updateIsNew(isNew)
                    }
                }
            }
        }
    }

    fun updateId(value: String) {
        screenState = screenState.copy(id = value)
    }

    fun updateCreatedAt(value: Long) {
        screenState = screenState.copy(createdAt = value)
    }

    fun updateTitle(value: String) {
        screenState = screenState.copy(title = value)
    }

    fun updateDescription(value: String) {
        screenState = screenState.copy(description = value)
    }

    fun updateThumbnail(value: String) {
        screenState = screenState.copy(thumbnail = value)
    }

    fun updateThumbnailUploaderState(value: RequestState<Unit>) {
        thumbnailUploaderState = value
    }

    fun updateCategory(value: ProductCategory) {
        screenState = screenState.copy(category = value)
    }

    fun updateFlavors(value: String) {
        screenState = screenState.copy(flavors = value)
    }

    fun updateWeight(value: Int?) {
        screenState = screenState.copy(weight = value)
    }

    fun updatePrice(value: Double) {
        screenState = screenState.copy(price = value)
    }

    fun updateIsNew(value: Boolean) {
        screenState = screenState.copy(isNew = value)
    }

    fun updateIsPopular(value: Boolean) {
        screenState = screenState.copy(isPopular = value)
    }

    fun updateIsDiscounted(value: Boolean) {
        screenState = screenState.copy(isDiscounted = value)
    }

    fun submitProduct(
        isUpdatingProduct: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val product = screenState.toProduct(isUpdatingProduct = isUpdatingProduct)

            if (isUpdatingProduct) {
                adminRepository.updateProduct(product, onSuccess, onError)
            } else {
                adminRepository.createNewProduct(product, onSuccess, onError)
            }
        }
    }

    fun uploadThumbnailToStorage(
        file: File?,
        onSuccess: () -> Unit,
    ) {
        if (file == null) {
            updateThumbnailUploaderState(RequestState.Error("File is null. Error while selecting an image."))
            return
        }

        updateThumbnailUploaderState(RequestState.Loading)

        viewModelScope.launch {
            try {
                val downloadUrl = adminRepository.uploadImageToStorage(file)

                if (downloadUrl.isNullOrEmpty()) {
                    throw Exception("Failed to retrieve a download URL after the upload.")
                }

                productId.takeIf { it.isNotEmpty() }?.let { id ->
                    adminRepository.updateProductThumbnail(
                        productId = id,
                        downloadUrl = downloadUrl,
                        onSuccess = {
                            onSuccess()
                            updateThumbnailUploaderState(RequestState.Success(Unit))
                            updateThumbnail(downloadUrl)
                        },
                        onError = { message ->
                            updateThumbnailUploaderState(RequestState.Error(message))
                        }
                    )
                } ?: run {
                    onSuccess()
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateThumbnail(downloadUrl)
                }
            } catch (e: Exception) {
                updateThumbnailUploaderState(RequestState.Error("Error while uploading: $e"))
            }
        }
    }

    fun deleteThumbnailFromStorage(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            adminRepository.deleteImageFromStorage(
                downloadUrl = screenState.thumbnail,
                onSuccess = {
                    productId.takeIf { it.isNotEmpty() }?.let { id ->
                        viewModelScope.launch {
                            adminRepository.updateProductThumbnail(
                                productId = id,
                                downloadUrl = "",
                                onSuccess = {
                                    updateThumbnail(value = "")
                                    updateThumbnailUploaderState(RequestState.Idle)
                                    onSuccess()
                                },
                                onError = { message -> onError(message) }
                            )
                        }
                    } ?: run {
                        updateThumbnail(value = "")
                        updateThumbnailUploaderState(RequestState.Idle)
                        onSuccess()
                    }
                },
                onError = onError
            )
        }
    }
    /**
     * We trigger onSuccess when deleting the product because is more important
     * to delete the product that the thumbnail from the database */
    fun deleteProduct(onSuccess: () -> Unit, onError: (String) -> Unit) {
        productId.takeIf { it.isNotEmpty() }?.let { id ->
            viewModelScope.launch {
                adminRepository.deleteProduct(
                    productId = id,
                    onSuccess = {
                        deleteThumbnailFromStorage(
                            onSuccess = {},
                            onError = {}
                        )
                        onSuccess()
                    },
                    onError = { message -> onError(message) }
                )
            }
        }
    }
}