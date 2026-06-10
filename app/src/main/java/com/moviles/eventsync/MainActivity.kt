package com.moviles.eventsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.eventsync.data.TokenManager
import com.moviles.eventsync.data.network.RetrofitClient
import com.moviles.eventsync.data.repository.AuthRepository
import com.moviles.eventsync.ui.auth.LoginViewModel
import com.moviles.eventsync.ui.auth.RegisterViewModel
import com.moviles.eventsync.ui.screens.login.LoginScreen
import com.moviles.eventsync.ui.screens.login.RegisterScreen
import com.moviles.eventsync.ui.screens.main.MainScreen
import com.moviles.eventsync.ui.theme.EventSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val tokenManager = TokenManager(this)
        val authRepository = AuthRepository(RetrofitClient.getApiService(tokenManager))

        setContent {
            EventSyncTheme {
                val tokenState by tokenManager.token.collectAsState(initial = null)
                var currentScreen by remember { mutableStateOf<String?>(null) }
                
                LaunchedEffect(tokenState) {
                    if (currentScreen == null) {
                        currentScreen = if (tokenState != null) "home" else "login"
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "login" -> {
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return LoginViewModel(authRepository, tokenManager) as T
                                    }
                                }
                            )
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { token ->
                                    currentScreen = "home"
                                },
                                onNavigateToRegister = {
                                    currentScreen = "register"
                                }
                            )
                        }
                        "register" -> {
                            val registerViewModel: RegisterViewModel = viewModel(
                                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return RegisterViewModel(authRepository, tokenManager) as T
                                    }
                                }
                            )
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = {
                                    currentScreen = "login"
                                },
                                onNavigateToLogin = {
                                    currentScreen = "login"
                                }
                            )
                        }
                        "home" -> {
                            MainScreen(
                                onLogout = {
                                    currentScreen = "login"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
