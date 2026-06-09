package com.moviles.eventsync.ui.screens.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun NotificationsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Módulo 4: Bandeja de Entrada de Notificaciones")
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    EventSyncTheme {
        NotificationsScreen()
    }
}
