package com.moviles.eventsync.data.network

import com.moviles.eventsync.core.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EventSyncApi {

    @POST(AppConstants.LOGIN_PATH)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(AppConstants.REGISTER_PATH)
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @GET(AppConstants.EVENTS_PATH)
    suspend fun getEvents(): Response<List<EventResponse>>

    @GET("${AppConstants.EVENTS_PATH}/{id}")
    suspend fun getEventById(@retrofit2.http.Path("id") id: Int): Response<EventResponse>
}
