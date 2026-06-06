package com.moviles.eventsync.data.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val message: String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class EventResponse(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String
)
