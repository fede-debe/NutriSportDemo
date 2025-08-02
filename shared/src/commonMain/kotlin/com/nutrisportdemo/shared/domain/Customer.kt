package com.nutrisportdemo.shared.domain

import kotlinx.serialization.Serializable

/**
 * When we authenticate our user with a Google sign in,
 * Firebase will generate that ID for us and we don't need to generate it
 * */
@Serializable
data class Customer(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val phoneNumber: PhoneNumber? = null,
    val cart: List<CartItem> = emptyList(),
    val isAdmin: Boolean = false
)
@Serializable
data class PhoneNumber(
    val dialCode: Int,
    val number: String
)