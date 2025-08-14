package com.nutrisportdemo.shared.util


import com.nutrisportdemo.shared.navigation.Screen
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * multi-platform settings library.
 * So with this library we will be able to use that to local storage
 * or local preferences to save and read those values as well.
 *
 * So this libs.multiplatform.settings.make.observable artifact
 * will allow us to use a Kotlin flow with those persistent values.
 * So we're going to observe that flow from our navigation graph.
 * And whenever we update those values from this intent or
 * the deep link from our iOS side, then we're going to trigger
 * this logic to navigate to our payment completed screen.
 * */

@OptIn(ExperimentalSettingsApi::class)
object PreferencesRepository {
    /** This allow us to use a Kotlin flow with those persistent values --> put them and read them */
    private val settings: ObservableSettings = Settings().makeObservable()

    private const val IS_SUCCESS = "isSuccess_paypal"
    private const val ERROR = "error_paypal"
    private const val TOKEN = "token_paypal"

    fun savePayPalData(
        isSuccess: Boolean?,
        error: String?,
        token: String?,
    ) {
        isSuccess?.let { settings.putBoolean(IS_SUCCESS, it) }
        error?.let { settings.putString(ERROR, it) }
        token?.let { settings.putString(TOKEN, it) }
    }

    fun readPayPalDataFlow(): Flow<Screen.PaymentCompleted?> = callbackFlow {
        fun getCurrentPaymentProcessed(): Screen.PaymentCompleted {
            return Screen.PaymentCompleted(
                isSuccess = settings.getBooleanOrNull(IS_SUCCESS),
                error = settings.getStringOrNull(ERROR),
                token = settings.getStringOrNull(TOKEN)
            )
        }

        while (true) {
            this.send(getCurrentPaymentProcessed())
            delay(1000) // Check for updates every second
        }
    }

    fun reset() {
        settings.clear()
    }
}