package com.federico.data

import com.federico.data.domain.CustomerRepository
import com.nutrisportdemo.shared.domain.Customer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.firestore

class CustomerRepositoryImpl : CustomerRepository {
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
}