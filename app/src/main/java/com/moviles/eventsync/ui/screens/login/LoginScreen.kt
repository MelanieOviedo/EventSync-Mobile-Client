package com.moviles.eventsync.ui.screens.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.eventsync.R
import com.moviles.eventsync.data.network.RetrofitClient
import com.moviles.eventsync.data.repository.AuthRepository
import com.moviles.eventsync.ui.auth.LoginState
import com.moviles.eventsync.ui.auth.LoginViewModel
import com.moviles.eventsync.ui.components.EventSyncButton
import com.moviles.eventsync.ui.components.EventSyncTextField
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- LOGO DE LA APP ---
        Image(
            painter = painterResource(id = R.drawable.logo_inicio),
            contentDescription = "Logo de EventSync",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Aumentamos el tamaño para que se lea el eslogan del logo
                .padding(bottom = 16.dp)
        )

        // Eliminamos los Text() de "EventSync" porque ya vienen dentro de la imagen logo_inicio.png

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

        Spacer(modifier = Modifier.height(16.dp))

        when (val currentState = state) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Error -> {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            is LoginState.Success -> {
                LaunchedEffect(currentState.token) {
                    onLoginSuccess(currentState.token)
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(8.dp))

        EventSyncButton(
            text = "Iniciar Sesión",
            onClick = { viewModel.login(email, password) },
            enabled = state !is LoginState.Loading
        )

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
    val authRepository = AuthRepository(RetrofitClient.apiService)
    val viewModel: LoginViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(authRepository) as T
            }
        }
    )
    EventSyncTheme {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}
