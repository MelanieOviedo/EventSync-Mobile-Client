package com.moviles.eventsync.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.network.EventResponse
import com.moviles.eventsync.data.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventDetailState {
    object Loading : EventDetailState()
    data class Success(val event: EventResponse) : EventDetailState()
    data class Error(val message: String) : EventDetailState()
}

class EventDetailViewModel(private val repository: EventsRepository) : ViewModel() {

    private val _state = MutableStateFlow<EventDetailState>(EventDetailState.Loading)
    val state: StateFlow<EventDetailState> = _state.asStateFlow()

    fun getEventById(id: Int) {
        viewModelScope.launch {
            _state.value = EventDetailState.Loading
            repository.getEventById(id)
                .onSuccess { event ->
                    _state.value = EventDetailState.Success(event)
                }
                .onFailure { error ->
                    _state.value = EventDetailState.Error(error.message ?: "Error desconocido")
                }
        }
    }
}
