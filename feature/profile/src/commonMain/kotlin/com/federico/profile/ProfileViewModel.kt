package com.federico.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.federico.data.domain.CustomerRepository
import com.nutrisportdemo.shared.domain.Country
import com.nutrisportdemo.shared.domain.PhoneNumber
import com.nutrisportdemo.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country = Country.Serbia,
    val phoneNumber: PhoneNumber? = null,
)

class ProfileViewModel(
    private val customerRepository: CustomerRepository,
) : ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)

    /** Private set to update this property only within this viewModel */
    var screenState: ProfileScreenState by mutableStateOf(ProfileScreenState())
        private set

    init {
        viewModelScope.launch {
            customerRepository.readCustomerFlow().collectLatest { data ->
                when {
                    data.isSuccess() -> {
                        val fetchedCustomer = data.getSuccessData()
                        fetchedCustomer.apply {
                            screenState = ProfileScreenState(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                city = city,
                                postalCode = postalCode,
                                address = address,
                                country = Country.entries.firstOrNull { it.dialCode == fetchedCustomer.phoneNumber?.dialCode }
                                    ?: Country.Serbia,
                                phoneNumber = phoneNumber
                            )
                        }
                        screenReady = RequestState.Success(Unit)
                    }

                    data.isError() -> {
                        screenReady = RequestState.Error(data.getErrorMessage())
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateFirstName(value: String) {
        screenState = screenState.copy(firstName = value)
    }

    fun updateLastName(value: String) {
        screenState = screenState.copy(lastName = value)
    }

    fun updateCity(value: String) {
        screenState = screenState.copy(city = value)
    }

    fun updatePostalCode(value: Int?) {
        screenState = screenState.copy(postalCode = value)
    }

    fun updateAddress(value: String) {
        screenState = screenState.copy(address = value)
    }

    fun updateCountry(value: Country) {
        screenState = screenState.copy(country = value)
    }

    fun updatePhoneNumber(value: String) {
        screenState =
            screenState
                .copy(
                    phoneNumber = PhoneNumber(
                        dialCode = screenState.country.dialCode,
                        number = value
                    )
                )
    }
}