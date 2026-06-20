package com.moviles.eventsync.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.moviles.eventsync.data.TokenManager
import com.moviles.eventsync.data.network.RegisterRequest
import com.moviles.eventsync.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

                    // Obtener y enviar el token de FCM tras el registro exitoso
                    try {
                        val fcmToken = FirebaseMessaging.getInstance().token.await()
                        repository.updateFcmToken(fcmToken)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                _state.value = RegisterState.Success
            }.onFailure { error ->
                _state.value = RegisterState.Error(error.message ?: "Error en el registro")
            }
        }
    }
}
