package com.moviles.eventsync.data.repository

import com.moviles.eventsync.data.network.BookingResponse
import com.moviles.eventsync.data.network.EventResponse
import com.moviles.eventsync.data.network.EventSyncApi
import com.moviles.eventsync.data.network.ReservationRequest
import com.moviles.eventsync.data.network.ReservationResponse
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

    suspend fun makeReservation(eventId: Int): Result<ReservationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.makeReservation(ReservationRequest(eventId))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al realizar la reserva: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getMyBookings(): Result<List<BookingResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getMyBookings()
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener mis reservas: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun cancelReservation(eventId: Int): Result<ReservationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.cancelReservation(eventId)
                if (response.isSuccessful) {
                    val body = response.body() ?: ReservationResponse("Reserva cancelada exitosamente")
                    Result.success(body)
                } else {
                    val errorBody = response.errorBody()?.string()
                    // Intentar extraer el mensaje del JSON {"message": "..."}
                    val errorMsg = try {
                        val json = com.google.gson.JsonParser.parseString(errorBody)
                        json.asJsonObject.get("message").asString
                    } catch (e: Exception) {
                        errorBody ?: response.message()
                    }
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
