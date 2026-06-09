package com.moviles.eventsync.ui.screens.reservations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun ReservationsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Módulo 3: Mis Reservas Activas e Historial")
    }
}

@Preview(showBackground = true)
@Composable
fun ReservationsScreenPreview() {
    EventSyncTheme {
        ReservationsScreen()
    }
}
