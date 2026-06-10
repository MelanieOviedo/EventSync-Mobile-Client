package com.moviles.eventsync.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.TokenManager
import com.moviles.eventsync.data.network.RegisterRequest
import com.moviles.eventsync.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = RegisterState.Error("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            _state.value = RegisterState.Loading
            val result = repository.register(RegisterRequest(name, email, password))
            result.onSuccess { response ->
                if (response.token != null) {
                    tokenManager.saveToken(response.token)
                }
                _state.value = RegisterState.Success
            }.onFailure { error ->
                _state.value = RegisterState.Error(error.message ?: "Error en el registro")
            }
        }
    }
}
