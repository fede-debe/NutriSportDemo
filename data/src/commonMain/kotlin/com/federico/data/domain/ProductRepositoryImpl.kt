package com.federico.data.domain

import com.federico.mapper.toProduct
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class ProductRepositoryImpl: ProductRepository {
    override fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    override fun readDiscountedProducts(): Flow<RequestState<List<Product>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore
                    // if we declare multiple where conditions, all of them has to be true to
                    // return a value from the collection
                    database.collection("product")
//                        .where { "isNew" equalTo true }
                        .where { "isDiscounted" equalTo true }
                        .snapshots
                        .collectLatest { query ->
                            val products = query.documents.map { document -> document.toProduct() }
                            send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase()) }))
                        }
                } else {
                    send(RequestState.Error("User is not available."))
                }
            } catch (e: Exception) {
                send(RequestState.Error("Error while reading the last 10 items from the database: ${e.message}"))
            }
        }

    override fun readNewProducts(): Flow<RequestState<List<Product>>> {
        TODO("Not yet implemented")
    }
}