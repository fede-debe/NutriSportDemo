package com.federico.products_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.federico.data.domain.ProductRepository
import com.nutrisportdemo.shared.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ProductsOverviewViewModel(
    productRepository: ProductRepository
) : ViewModel() {
    val products = productRepository.readDiscountedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )
}