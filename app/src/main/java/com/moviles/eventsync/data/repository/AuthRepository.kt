package com.moviles.eventsync.data.repository

import com.moviles.eventsync.data.network.EventSyncApi
import com.moviles.eventsync.data.network.FcmTokenRequest
import com.moviles.eventsync.data.network.LoginRequest
import com.moviles.eventsync.data.network.LoginResponse
import com.moviles.eventsync.data.network.RegisterRequest

class AuthRepository(private val api: EventSyncApi) {

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message().ifEmpty { "Error en el inicio de sesión" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<LoginResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message().ifEmpty { "Error en el registro" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFcmToken(token: String): Result<Unit> {
        return try {
            val response = api.updateFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar token FCM"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(imagePart: okhttp3.MultipartBody.Part): Result<Unit> {
        return try {
            val response = api.uploadProfileImage(imagePart)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al subir la imagen de perfil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): Result<com.moviles.eventsync.data.network.UserProfileResponse> {
        return try {
            val response = api.getUserProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener el perfil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(current: String, new: String): Result<Unit> {
        return try {
            val response = api.changePassword(com.moviles.eventsync.data.network.ChangePasswordRequest(current, new))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = if (response.code() == 400) "La contraseña actual es incorrecta" else "Error al cambiar la contraseña"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
