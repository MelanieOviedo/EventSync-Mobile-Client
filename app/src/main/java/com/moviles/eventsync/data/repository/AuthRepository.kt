package com.moviles.eventsync.data.repository

import com.moviles.eventsync.data.network.EventSyncApi
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
}
