package com.moviles.eventsync.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.network.EventResponse
import com.moviles.eventsync.data.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EventsState {
    object Loading : EventsState()
    data class Success(val events: List<EventResponse>) : EventsState()
    data class Error(val message: String) : EventsState()
}

class EventsViewModel(private val repository: EventsRepository) : ViewModel() {

    private val _state = MutableStateFlow<EventsState>(EventsState.Loading)
    val state: StateFlow<EventsState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _state.value = EventsState.Loading
            val result = repository.getEvents()
            result.onSuccess { events ->
                _state.value = EventsState.Success(events)
            }.onFailure { error ->
                _state.value = EventsState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.getEvents()
            result.onSuccess { events ->
                _state.value = EventsState.Success(events)
            }.onFailure { error ->
                // Opcionalmente podemos mantener el estado previo si hay error en el refresh
                // o mostrar un error. Por ahora seguimos el patrón de fetchEvents.
                _state.value = EventsState.Error(error.message ?: "Error desconocido")
            }
            _isRefreshing.value = false
        }
    }
}
