package com.moviles.eventsync.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.eventsync.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class ProfileImageState {
    object Idle : ProfileImageState()
    object Loading : ProfileImageState()
    object Success : ProfileImageState()
    data class Error(val message: String) : ProfileImageState()
}

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

    private val _imageState = MutableStateFlow<ProfileImageState>(ProfileImageState.Idle)
    val imageState: StateFlow<ProfileImageState> = _imageState

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

    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _imageState.value = ProfileImageState.Loading
            
            try {
                val file = uriToFile(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                
                val result = repository.uploadProfileImage(body)
                result.onSuccess {
                    _imageState.value = ProfileImageState.Success
                }.onFailure { error ->
                    _imageState.value = ProfileImageState.Error(error.message ?: "Error al subir imagen")
                }
            } catch (e: Exception) {
                _imageState.value = ProfileImageState.Error("No se pudo procesar la imagen")
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "profile_upload.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    fun resetImageState() {
        _imageState.value = ProfileImageState.Idle
    }

    fun resetPasswordState() {
        _passwordState.value = ChangePasswordState.Idle
    }
}
