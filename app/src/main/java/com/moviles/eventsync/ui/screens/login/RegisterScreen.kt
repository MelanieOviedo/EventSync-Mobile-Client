package com.moviles.eventsync.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear Cuenta",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Únete a EventSync y reserva tu lugar",
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        EventSyncTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nombre Completo",
            leadingIcon = Icons.Default.Person
        )

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

        EventSyncButton(
            text = "Registrarse",
            onClick = onRegisterSuccess
        )

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    EventSyncTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegisterScreen(
                onRegisterSuccess = {},
                onNavigateToLogin = {}
            )
        }
    }
}
