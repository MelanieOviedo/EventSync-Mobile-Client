package com.moviles.eventsync.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val id: Int = 0,
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _passwordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val passwordState: StateFlow<ChangePasswordState> = _passwordState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getUserProfile().onSuccess { profile ->
                _uiState.value = _uiState.value.copy(
                    id = profile.id,
                    fullName = profile.fullName,
                    email = profile.email,
                    role = profile.role,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar perfil"
                )
            }
        }
    }

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _passwordState.value = ChangePasswordState.Loading
            repository.changePassword(current, new).onSuccess {
                _passwordState.value = ChangePasswordState.Success
            }.onFailure { error ->
                _passwordState.value = ChangePasswordState.Error(error.message ?: "Error al cambiar contraseña")
            }
        }
    }

    fun resetPasswordState() {
        _passwordState.value = ChangePasswordState.Idle
    }
}
