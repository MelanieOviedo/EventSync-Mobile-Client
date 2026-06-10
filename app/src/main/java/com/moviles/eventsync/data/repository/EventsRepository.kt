package com.moviles.eventsync.data.repository

import com.moviles.eventsync.data.network.EventResponse
import com.moviles.eventsync.data.network.EventSyncApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventsRepository(private val api: EventSyncApi) {

    suspend fun getEvents(): Result<List<EventResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getEvents()
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener eventos: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getEventById(id: Int): Result<EventResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getEventById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener detalle del evento: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
