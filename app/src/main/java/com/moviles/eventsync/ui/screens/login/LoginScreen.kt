package com.moviles.eventsync.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.eventsync.ui.components.EventSyncButton
import com.moviles.eventsync.ui.components.EventSyncTextField
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo o Título
        Text(
            text = "EventSync",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Reserva tu lugar en los mejores eventos",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campos de entrada
        EventSyncTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo Electrónico",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        EventSyncTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            leadingIcon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Inicio de Sesión
        EventSyncButton(
            text = "Iniciar Sesión",
            onClick = { onLoginSuccess() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opción de registro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "¿No tienes cuenta? ")
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Regístrate aquí",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EventSyncTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }
    }
}
