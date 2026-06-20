package com.moviles.eventsync.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.network.NotificationResponse
import com.moviles.eventsync.data.repository.NotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationsState {
    object Loading : NotificationsState()
    data class Success(val notifications: List<NotificationResponse>) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

class NotificationsViewModel(private val repository: NotificationsRepository) : ViewModel() {

    private val _state = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun fetchNotifications() {
        viewModelScope.launch {
            if (!_isRefreshing.value) _state.value = NotificationsState.Loading
            val result = repository.getNotifications()
            result.onSuccess {
                _state.value = NotificationsState.Success(it)
            }.onFailure {
                _state.value = NotificationsState.Error(it.message ?: "Error desconocido")
            }
            _isRefreshing.value = false
        }
    }

    fun refreshNotifications() {
        _isRefreshing.value = true
        fetchNotifications()
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            val result = repository.markAsRead(notificationId)
            result.onSuccess {
                // Mejora: Actualizamos el estado local inmediatamente
                val currentState = _state.value
                if (currentState is NotificationsState.Success) {
                    val updatedList = currentState.notifications.map {
                        if (it.id == notificationId) it.copy(isRead = true) else it
                    }
                    _state.value = NotificationsState.Success(updatedList)
                }
            }.onFailure {
                // Opcional: podrías mostrar un error si falla la red
            }
        }
    }
}
