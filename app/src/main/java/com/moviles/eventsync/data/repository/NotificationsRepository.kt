package com.moviles.eventsync.data.repository

import com.moviles.eventsync.data.network.EventSyncApi
import com.moviles.eventsync.data.network.NotificationResponse

class NotificationsRepository(private val api: EventSyncApi) {

    suspend fun getNotifications(): Result<List<NotificationResponse>> {
        return try {
            val response = api.getNotifications()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener notificaciones"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(notificationId: Int): Result<Unit> {
        return try {
            val response = api.markAsRead(notificationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al marcar como leída"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
