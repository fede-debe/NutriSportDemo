package com.federico.checkout.domain

import com.nutrisportdemo.shared.Constants.PAYPAL_CHECKOUT_ENDPOINT
import com.nutrisportdemo.shared.Constants.PAYPAL_GET_TOKEN_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import openWebBrowser
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaypalApi {
    /**
     * So by default Kotlin access serialization omits fields with default values when converting an object to JSON.
     * When you set this, encode defaults to true, it forces all fields, including those with default values,
     * to be included in the serialized JSON. So this is useful because it ensures that the JSON payload includes
     * all fields which some APIs may require explicitly. This also helps to avoid surprises when the server
     * expects the keys to exist, but they are skipped due to have a default values.
     *
     * This setting is there to make sure the client always sends every field to the server,
     * preventing issues where the server requires a field but Kotlin’s default serialization would leave it out.
     * */
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
    }

    /**
     * property that will be used to hold the value of that access
     * token that we retrieve from the PayPal REST API.
     * */
    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken.asStateFlow()

    suspend fun fetchAccessToken(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            // Call your Firebase Function instead of PayPal directly (no secrets in app)
            val response = client.post(urlString = PAYPAL_GET_TOKEN_URL) {
                contentType(ContentType.Application.Json)
                setBody("{}") // no payload needed for this MVP endpoint
            }

            if (response.status == HttpStatusCode.OK) {
                val dto = response.body<PaypalTokenDto>()
                _accessToken.value = dto.accessToken
                onSuccess(dto.accessToken)
            } else {
                onError("Error while fetching an Access Token: ${response.status} - ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            onError("Error while fetching an Access Token: ${e.message}")
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    suspend fun beginCheckout(
        amount: Amount,
        fullName: String,
        shippingAddress: ShippingAddress,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (_accessToken.value.isEmpty()) {
            onError("Error while starting the checkout: Access Token is empty.")
            return
        }

        val uniqueId = Uuid.random().toHexString()
        val orderRequest = OrderRequest(
            purchaseUnits = listOf(
                PurchaseUnit(
                    referenceId = uniqueId,
                    amount = amount,
                    shipping = Shipping(
                        name = Name(fullName = fullName),
                        address = shippingAddress
                    )
                )
            )
        )

        val response = client.post(urlString = PAYPAL_CHECKOUT_ENDPOINT) {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${_accessToken.value}")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append("PayPal-Request-Id", uniqueId)
            }
            setBody(orderRequest)
        }

        if (response.status == HttpStatusCode.OK) {
            val orderResponse = response.body<OrderResponse>()
            val payerLink = orderResponse.links.firstOrNull { it.rel == "payer-action" }?.href

            withContext(Dispatchers.Main) {
                handleUrl(
                    url = payerLink,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        } else {
            onError("Error while starting the checkout: ${response.status} - ${response.bodyAsText()}")
        }
    }

    private fun handleUrl(
        url: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (url == null) {
            onError("Error while opening a web browser: URL is null")
            return
        }

        openWebBrowser(url = url)
        onSuccess()
    }
}