package com.federico.data

import com.federico.data.domain.AdminRepository
import com.nutrisportdemo.shared.domain.Product
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
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
                productCollection.document(product.id).set(product)
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
}