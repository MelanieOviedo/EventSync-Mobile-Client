package com.moviles.eventsync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.moviles.eventsync.data.TokenManager
import com.moviles.eventsync.data.network.RetrofitClient
import com.moviles.eventsync.data.repository.AuthRepository
import com.moviles.eventsync.ui.auth.LoginViewModel
import com.moviles.eventsync.ui.auth.RegisterViewModel
import com.moviles.eventsync.ui.screens.login.LoginScreen
import com.moviles.eventsync.ui.screens.login.RegisterScreen
import com.moviles.eventsync.ui.screens.main.MainScreen
import com.moviles.eventsync.ui.theme.EventSyncTheme
import kotlinx.coroutines.tasks.await

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
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Permiso concedido o denegado
                }
                
                LaunchedEffect(tokenState) {
                    if (currentScreen == null) {
                        currentScreen = if (tokenState != null) "home" else "login"
                    }
                    
                    // Si el usuario está logueado, aprovechamos para asegurar que el token FCM esté actualizado
                    if (tokenState != null) {
                        try {
                            val fcmToken = FirebaseMessaging.getInstance().token.await()
                            authRepository.updateFcmToken(fcmToken)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
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
