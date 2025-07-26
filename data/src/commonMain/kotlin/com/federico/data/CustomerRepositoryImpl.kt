package com.federico.data

import com.federico.data.domain.CustomerRepository
import com.nutrisportdemo.shared.domain.Customer
import com.nutrisportdemo.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class CustomerRepositoryImpl : CustomerRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (user != null) {
                val customerCollection = Firebase.firestore.collection("customer")
                val fullName = user.displayName?.trim().orEmpty().split(" ")
                val firstName = fullName.firstOrNull() ?: "Unknown"
                val lastName = if (fullName.size > 1) {
                    fullName.subList(1, fullName.size).joinToString(" ")
                } else {
                    "Unknown"
                }

                val customer = Customer(
                    id = user.uid,
                    firstName = firstName,
                    lastName = lastName,
                    email = user.email ?: "Unknown"
                )

                val customerExists = customerCollection.document(user.uid).get().exists

                if (customerExists) {
                    onSuccess()
                } else {
                    customerCollection.document(user.uid).set(customer)
                    onSuccess()
                }
            } else {
                onError("User is not available")
            }
        } catch (e: Exception) {
            onError("Error while creating a Customer: ${e.message}")
        }
    }

    override fun readCustomerFlow(): Flow<RequestState<Customer>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            when {
                // authenticated user
                userId != null -> {
                    val database = Firebase.firestore
                    database.collection("customer")
                        .document(userId).snapshots.collectLatest { document ->
                        when {
                            document.exists -> {
                                /** When this customer changes, the flow will update and send the updated version to the UI */
                                val customer = Customer(
                                    id = document.id,
                                    firstName = document.get(field = "firstName"),
                                    lastName = document.get(field = "lastName"),
                                    email = document.get(field = "email"),
                                    city = document.get(field = "city"),
                                    postalCode = document.get(field = "postalCode"),
                                    address = document.get(field = "address"),
                                    phoneNumber = document.get(field = "phoneNumber"),
                                    cart = document.get(field = "cart")
                                )
                                send(RequestState.Success(data = customer))
                            }

                            else -> send(RequestState.Error("Queried customer document does not exist"))
                        }
                    }
                }

                else -> send(RequestState.Error("User is not available"))

            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading a customer information ${e.message}"))

        }
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error("Error while signing out: ${e.message}")
        }
    }
}