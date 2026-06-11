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

    @POST(AppConstants.BOOKINGS_PATH)
    suspend fun makeReservation(@Body request: ReservationRequest): Response<ReservationResponse>

    @GET(AppConstants.BOOKINGS_PATH)
    suspend fun getMyBookings(): Response<List<BookingResponse>>

    @retrofit2.http.DELETE("${AppConstants.BOOKINGS_PATH}/event/{eventId}")
    suspend fun cancelReservation(@retrofit2.http.Path("eventId") eventId: Int): Response<ReservationResponse>

    @retrofit2.http.PUT("${AppConstants.USERS_PATH}/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<Unit>

    @GET(AppConstants.NOTIFICATIONS_PATH)
    suspend fun getNotifications(): Response<List<NotificationResponse>>

    @retrofit2.http.PUT("${AppConstants.NOTIFICATIONS_PATH}/{id}/read")
    suspend fun markAsRead(@retrofit2.http.Path("id") id: Int): Response<Unit>

    @retrofit2.http.Multipart
    @retrofit2.http.PUT("${AppConstants.USERS_PATH}/profile-image")
    suspend fun uploadProfileImage(@retrofit2.http.Part image: okhttp3.MultipartBody.Part): Response<Unit>

    @GET("${AppConstants.USERS_PATH}/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @POST("${AppConstants.USERS_PATH}/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>
}
