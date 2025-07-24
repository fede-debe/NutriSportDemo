package com.federico.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.federico.data.domain.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeGraphViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun signOut(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                customerRepository.signOut()
            }

            when {
                result.isSuccess() -> onSuccess()
                result.isError() -> onError(result.getErrorMessage())
            }
        }
    }
}