package com.federico.data.domain

import com.nutrisportdemo.shared.domain.CartItem
import com.nutrisportdemo.shared.domain.Customer
import com.nutrisportdemo.shared.util.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCurrentUserId(): String?
    suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun readCustomerFlow(): Flow<RequestState<Customer>>
    suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun addItemToCard(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    /** use Unit as no value, we could use a Boolean to return if we succeeded or not.
     * Since the RequestState class contains the error and success state,
     * we don't need to return a  value and use Unit.
     * */
    suspend fun signOut(): RequestState<Unit>
}