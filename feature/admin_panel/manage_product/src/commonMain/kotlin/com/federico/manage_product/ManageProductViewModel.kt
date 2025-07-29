package com.federico.manage_product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.federico.data.domain.AdminRepository
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.ProductCategory
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ManageProductState @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toHexString(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val category: ProductCategory = ProductCategory.Protein,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
) : ViewModel() {
    var screenState by mutableStateOf(ManageProductState())
        private set

    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0


    fun updateTitle(value: String) {
        screenState = screenState.copy(title = value)
    }

    fun updateDescription(value: String) {
        screenState = screenState.copy(description = value)
    }

    fun updateThumbnail(value: String) {
        screenState = screenState.copy(thumbnail = value)
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

    fun createNewProduct(onSuccessful: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            screenState.apply {
                adminRepository.createNewProduct(
                    product = Product(
                        id = id,
                        title = title,
                        description = description,
                        thumbnail = thumbnail,
                        category = category.name,
                        flavors = flavors.split(","),
                        weight = weight,
                        price = price
                    ),
                    onSuccess = onSuccessful,
                    onError = onError
                )
            }
        }
    }
}