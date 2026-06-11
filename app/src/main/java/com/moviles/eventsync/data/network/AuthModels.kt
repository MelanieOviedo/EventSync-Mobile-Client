package com.moviles.eventsync.data.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String? = null
)

data class RegisterRequest(
    @SerializedName("fullName") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class EventResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("date") val date: String,
    @SerializedName("maxCapacity") val maxCapacity: Int,
    @SerializedName("availableSpots") val availableSpots: Int,
    @SerializedName("imagePath") val imagePath: String? = null,
    @SerializedName("isReserved") val isReserved: Boolean = false
)

data class ReservationRequest(
    @SerializedName("eventId") val eventId: Int
)

data class ReservationResponse(
    @SerializedName("message") val message: String
)

data class BookingResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("eventId") val eventId: Int,
    @SerializedName("eventTitle") val eventTitle: String,
    @SerializedName("eventDate") val eventDate: String,
    @SerializedName("bookingDate") val bookingDate: String,
    @SerializedName("status") val status: String
)

data class FcmTokenRequest(
    @SerializedName("token") val token: String
)

data class NotificationResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("sentDate") val sentDate: String,
    @SerializedName("isRead") val isRead: Boolean
)

data class UserProfileResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)

data class ChangePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)
