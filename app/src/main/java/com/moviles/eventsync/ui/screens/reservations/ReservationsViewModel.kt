package com.moviles.eventsync.ui.screens.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.network.BookingResponse
import com.moviles.eventsync.data.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BookingsState {
    object Idle : BookingsState()
    object Loading : BookingsState()
    data class Success(val bookings: List<BookingResponse>) : BookingsState()
    data class Error(val message: String) : BookingsState()
}

class ReservationsViewModel(private val repository: EventsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BookingsState>(BookingsState.Idle)
    val uiState: StateFlow<BookingsState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.value = BookingsState.Loading
            val result = repository.getMyBookings()
            result.onSuccess { bookings ->
                _uiState.value = BookingsState.Success(bookings)
            }.onFailure { exception ->
                _uiState.value = BookingsState.Error(exception.message ?: "Error desconocido")
            }
        }
    }

    fun refreshBookings() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.getMyBookings()
            result.onSuccess { bookings ->
                _uiState.value = BookingsState.Success(bookings)
            }.onFailure { exception ->
                _uiState.value = BookingsState.Error(exception.message ?: "Error desconocido")
            }
            _isRefreshing.value = false
        }
    }
}
