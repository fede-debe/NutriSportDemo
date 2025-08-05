package com.federico.data

import com.federico.data.domain.CustomerRepository
import com.nutrisportdemo.shared.domain.CartItem
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
                    customerCollection.document(user.uid)
                        .collection("privateData")
                        .document("role")
                        .set(mapOf("isAdmin" to false))
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
                                val privateDataDocument = database.collection("customer")
                                    .document(userId)
                                    .collection("privateData")
                                    .document("role")
                                    .get()

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
                                    cart = document.get(field = "cart"),
                                    isAdmin = privateDataDocument.get(field = "isAdmin")
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

    override suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                val customerCollection = firestore.collection(collectionPath = "customer")

                val existingCustomer = customerCollection
                    .document(customer.id)
                    .get()

                if (existingCustomer.exists) {
                    customerCollection
                        .document(customer.id)
                        .update(
                            "firstName" to customer.firstName,
                            "lastName" to customer.lastName,
                            "city" to customer.city,
                            "postalCode" to customer.postalCode,
                            "address" to customer.address,
                            "phoneNumber" to customer.phoneNumber,

                        )
                    onSuccess()
                } else {
                    onError("Customer not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a Customer information: ${e.message}")
        }
    }

    override suspend fun addItemToCard(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")

                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    val existingCart = existingCustomer.get<List<CartItem>>("cart")
                    val updatedCart = existingCart + cartItem
                    /** merge = true to preserve the rest of existing data in the document.
                     * we are only updating  the cart field */
                    customerCollection.document(currentUserId)
                        .set(
                            data = mapOf("cart" to updatedCart),
                            merge = true
                        )
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while adding a product to cart: ${e.message}")
        }
    }

    override suspend fun updateCartItemQuantity(
        id: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")

                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    val existingCart = existingCustomer.get<List<CartItem>>("cart")
                    /** map existing cart list, and update quantity for the specific cart item */
                    val updatedCart = existingCart.map { cartItem ->
                        if (cartItem.id == id) {
                            cartItem.copy(quantity = quantity)
                        } else cartItem
                    }
                    /** update cart field with the updated cart list quantity*/
                    customerCollection.document(currentUserId)
                        .update(data = mapOf("cart" to updatedCart))
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a product to cart: ${e.message}")
        }
    }

    override suspend fun deleteCartItem(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")

                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    val existingCart = existingCustomer.get<List<CartItem>>("cart")
                    /** remove selected cart item from the list and then we update the list */
                    val updatedCart = existingCart.filterNot { it.id == id }
                    customerCollection.document(currentUserId)
                        .update(data = mapOf("cart" to updatedCart))
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while deleting a product from cart: ${e.message}")
        }
    }

    override suspend fun deleteAllCartItems(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
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