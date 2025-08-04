package com.federico.data.domain

import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getCurrentUserId(): String?
    fun readDiscountedProducts(): Flow<RequestState<List<Product>>>
    fun readNewProducts(): Flow<RequestState<List<Product>>>
}