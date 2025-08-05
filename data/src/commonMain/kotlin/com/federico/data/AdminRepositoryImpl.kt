package com.federico.data

import com.federico.data.domain.AdminRepository
import com.federico.mapper.toProductModel
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withTimeout
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AdminRepositoryImpl : AdminRepository {
    override fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    override suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val firestore = Firebase.firestore
                val productCollection = firestore.collection("product")
                // easier to query with lowercase
                productCollection.document(product.id)
                    .set(product.copy(title = product.title.lowercase()))
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while creating a new product: ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadImageToStorage(file: File): String? {
        return if (getCurrentUserId() != null) {
            val storage = Firebase.storage.reference
            val imagePath = storage.child("images/${Uuid.random().toHexString()}")
            try {
                /** This Firebase SDK by default is created in a way that if for example,
                 * you don't have an internet connection and you try to upload this image to storage,
                 * this function will just keep spinning without any return type or without any result.
                 * This is why we need to wrap it within the withTimeout(timeMillis = 20000L) function */
                withTimeout(timeMillis = 20000L) {
                    imagePath.putFile(file)
                    imagePath.getDownloadUrl()
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    override suspend fun deleteImageFromStorage(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val storagePath = extractFirebaseStoragePath(downloadUrl)
            when (storagePath) {
                null -> onError("Storage path is null")
                else -> {
                    Firebase.storage.reference(storagePath).delete()
                    onSuccess()
                }
            }
        } catch (e: Exception) {
            onError("Error while deleting a thumbnail: $e")
        }
    }

    override fun readLastTenProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection("product")
                    .orderBy("createdAt", Direction.DESCENDING)
                    .limit(10)
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

    // get single doc from firebase collection
    override suspend fun readProductById(productId: String): RequestState<Product> {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productDocument = database.collection("product")
                    .document(productId)
                    .get()
                if (productDocument.exists) {
                    val product = productDocument.toProductModel()
                    RequestState.Success(product.copy(title = product.title.uppercase()))
                } else {
                    RequestState.Error("Selected product not found.")
                }
            } else {
                RequestState.Error("User is not available.")
            }
        } catch (e: Exception) {
            RequestState.Error("Error while reading a selected product: ${e.message}")
        }
    }

    /** two functions will be helper functions to allow us to extract the actual
     *  image path from the download URL.
     */
    private fun extractFirebaseStoragePath(downloadUrl: String): String? {
        // startIndex should start after last char of the provided string
        val startIndex = downloadUrl.indexOf("/o/") + 3
        if (startIndex < 3) return null

        val endIndex = downloadUrl.indexOf("?", startIndex)
        val encodedPath = if (endIndex != -1) {
            downloadUrl.substring(startIndex, endIndex)
        } else {
            downloadUrl.substring(startIndex)
        }

        return decodeFirebaseStoragePath(encodedPath = encodedPath)
    }

    private fun decodeFirebaseStoragePath(encodedPath: String): String {
        return encodedPath
            .replace("%2F", "/")
            .replace("%20", " ")
    }

    override suspend fun updateProductThumbnail(
        productId: String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()

                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .update("thumbnail" to downloadUrl)
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    override suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(product.id)
                    .get()

                if (existingProduct.exists) {
                    productCollection.document(product.id)
                        .update(product.copy(title = product.title.lowercase()))
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a product: ${e.message}")
        }
    }

    override suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()

                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .delete()
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a product: ${e.message}")
        }
    }

    override fun searchProductByTitle(searchQuery: String): Flow<RequestState<List<Product>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
//                    val queryText = searchQuery.trim().lowercase()
//                    // special code used to simulate a start with, it used to text searching within firebase
//                    val endText = queryText + "\uf8ff"

                    val database = Firebase.firestore
                    database.collection("product")
//                        .orderBy("title")
//                        .startAt(queryText)
//                        .endAt(endText)
                        .snapshots
                        .collectLatest { query ->
                            val products = query.documents.map { document -> document.toProductModel() }
                            send(RequestState.Success(data = products
                                .filter { it.title.contains(searchQuery, ignoreCase = true) }
                                .map { it.copy(title = it.title.uppercase()) }))
                        }
                } else {
                    send(RequestState.Error("User is not available."))
                }

            } catch (e: Exception) {
                send(RequestState.Error("Error while searching a product: ${e.message}"))
            }
        }
}