package com.moviles.eventsync.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onLogout) {
            Text(text = "Cerrar Sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    EventSyncTheme {
        ProfileScreen(onLogout = {})
    }
}
