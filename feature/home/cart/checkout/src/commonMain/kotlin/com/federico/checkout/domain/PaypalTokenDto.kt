package com.federico.checkout.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaypalTokenDto(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("token_type")  val tokenType: String? = null,
    @SerialName("expires_in")  val expiresIn: Int? = null
)
