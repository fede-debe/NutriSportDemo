package com.federico.data

import com.federico.data.domain.ProductRepository
import com.federico.mapper.toProductModel
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.domain.ProductCategory
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
                    database.collection("product")
//                        .where { "isNew" equalTo true }
                        .where { "isDiscounted" equalTo true }
                        .snapshots
                        .collectLatest { query ->
                            val products =
                                query.documents.map { document -> document.toProductModel() }
                            send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase()) }))
                        }
                } else {
                    send(RequestState.Error("User is not available."))
                }
            } catch (e: Exception) {
                send(RequestState.Error("Error while reading the last 10 items from the database: ${e.message}"))
            }
        }

    override fun readNewProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                // if we declare multiple where conditions, all of them has to be true to
                // return a value from the collection
                database.collection("product")
                    .where { "isNew" equalTo true }
//                    .where { "isDiscounted" equalTo true }
                    .snapshots
                    .collectLatest { query ->
                        val products = query.documents.map { document -> document.toProductModel() }
                        send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase()) }))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading the last 10 items from the database: ${e.message}"))
        }
    }

    override fun readProductByIdFlow(id: String): Flow<RequestState<Product>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "product")
                    .document(id)
                    .snapshots
                    .collectLatest { document ->
                        if (document.exists) {
                            val product = document.toProductModel()
                            send(RequestState.Success(product.copy(title = product.title.uppercase())))
                        } else {
                            send(RequestState.Error("Selected product does not exist."))
                        }
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading a selected product: ${e.message}"))
        }
    }

    override fun readProductsByIdsFlow(ids: List<String>): Flow<RequestState<List<Product>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore
                    val productCollection = database.collection(collectionPath = "product")

                    /**
                     * creating multiple chunks because firebase query doesn't not support querying more
                     * than 10 items at once. We can't have more than 10 ids in a query.
                     *
                     * forEach chunk we execute a query the product collection, to add those products with
                     * the same ids from the chunk to a mutable list (allProducts).
                     *
                     * When we go through all those chunks (index == chunks.lastIndex), we can return all those products to the user
                     * stored into allProducts.
                     * */
                    val allProducts = mutableListOf<Product>()
                    val chunks = ids.chunked(10)

                    chunks.forEachIndexed { index, chunk ->
                        productCollection
                            .where { "id" inArray chunk }
                            .snapshots
                            .collectLatest { query ->
                                val products =
                                    query.documents.map { document -> document.toProductModel() }
                                allProducts.addAll(products.map { it.copy(title = it.title.uppercase()) })

                                if (index == chunks.lastIndex) {
                                    send(RequestState.Success(allProducts))
                                }
                            }
                    }
                } else {
                    send(RequestState.Error("User is not available."))
                }
            } catch (e: Exception) {
                send(RequestState.Error("Error while reading a selected product: ${e.message}"))
            }
        }

    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<RequestState<List<Product>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore
                    database.collection(collectionPath = "product")
                        // we don't store the entire object, only the name of the enum constant
                        .where { "category" equalTo category.name }
                        .snapshots
                        .collectLatest { query ->
                            val products =
                                query.documents.map { document -> document.toProductModel() }
                            send(RequestState.Success(products.map { it.copy(title = it.title.uppercase()) }))
                        }
                } else {
                    send(RequestState.Error("User is not available."))
                }
            } catch (e: Exception) {
                send(RequestState.Error("Error while reading a selected product: ${e.message}"))
            }
        }
}