package com.moviles.eventsync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object Main : Screen("main", "Main")
    
    // Bottom Nav Items
    object Events : Screen("events", "Inicio", Icons.Default.CalendarMonth)
    object Reservations : Screen("reservations", "Mis Reservas", Icons.Default.History)
    object Notifications : Screen("notifications", "Notificaciones", Icons.Default.Notifications)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    
    // Details
    object EventDetail : Screen("event_detail/{eventId}?isReserved={isReserved}", "Detalle de Evento") {
        fun createRoute(eventId: Int, isReserved: Boolean = false) = "event_detail/$eventId?isReserved=$isReserved"
    }
}

val bottomNavItems = listOf(
    Screen.Events,
    Screen.Reservations,
    Screen.Notifications,
    Screen.Profile
)
