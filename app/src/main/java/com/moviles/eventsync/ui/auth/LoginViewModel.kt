package com.moviles.eventsync.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.network.LoginRequest
import com.moviles.eventsync.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = repository.login(LoginRequest(email, password))
            result.onSuccess { response ->
                _state.value = LoginState.Success(response.token)
            }.onFailure { error ->
                _state.value = LoginState.Error(error.message ?: "Error en la conexión")
            }
        }
    }
}
