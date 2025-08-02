package com.federico.admin_panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.federico.data.domain.AdminRepository
import com.nutrisportdemo.shared.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class AdminPanelViewModel(
    adminRepository: AdminRepository
) : ViewModel() {

    private val products = adminRepository.readLastTenProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    /** contain filter list of products
     * debounce will execute this block after 15ms of delay
     * we need it because before searching through the database we need to set some delay
     * because the user start typing, it would be bad if we would search through the database
     * every millisecond the user types
     * */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredProducts = searchQuery
        .debounce(500)
        .flatMapLatest { query ->
            // if empty input, show current list
            if (query.isBlank()) products
            else adminRepository.searchProductByTitle(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )
}
