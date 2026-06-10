package com.moviles.eventsync.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.moviles.eventsync.data.TokenManager
import com.moviles.eventsync.data.network.RetrofitClient
import com.moviles.eventsync.data.repository.EventsRepository
import com.moviles.eventsync.navigation.Screen
import com.moviles.eventsync.ui.components.EventSyncBottomBar
import com.moviles.eventsync.ui.screens.details.EventDetailScreen
import com.moviles.eventsync.ui.screens.details.EventDetailViewModel
import com.moviles.eventsync.ui.screens.events.EventsScreen
import com.moviles.eventsync.ui.screens.events.EventsViewModel
import com.moviles.eventsync.ui.screens.notifications.NotificationsScreen
import com.moviles.eventsync.ui.screens.profile.ProfileScreen
import com.moviles.eventsync.ui.screens.reservations.ReservationsScreen
import com.moviles.eventsync.ui.screens.reservations.ReservationsViewModel
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()
    
    // Estado para el mensaje de éxito persistente
    var successBannerMessage by remember { mutableStateOf<String?>(null) }

    // Si hay un mensaje, lo quitamos después de un tiempo (ej. 5 segundos o más si el usuario quiere "minutos")
    LaunchedEffect(successBannerMessage) {
        if (successBannerMessage != null) {
            delay(8000) // 8 segundos para que sea visible
            successBannerMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            EventSyncBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Events.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Events.route) {
                val repository = EventsRepository(RetrofitClient.getApiService(tokenManager))
                val viewModel: EventsViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return EventsViewModel(repository) as T
                        }
                    }
                )
                
                LaunchedEffect(Unit) {
                    viewModel.fetchEvents()
                }

                EventsScreen(
                    viewModel = viewModel,
                    onEventClick = { eventId ->
                        navController.navigate(Screen.EventDetail.createRoute(eventId))
                    },
                    successMessage = successBannerMessage,
                    onDismissMessage = { successBannerMessage = null }
                )
            }

            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
                val repository = EventsRepository(RetrofitClient.getApiService(tokenManager))
                val viewModel: EventDetailViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return EventDetailViewModel(repository) as T
                        }
                    }
                )
                EventDetailScreen(
                    eventId = eventId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onReservationSuccess = { message ->
                        successBannerMessage = message
                    }
                )
            }
            composable(Screen.Reservations.route) {
                val repository = EventsRepository(RetrofitClient.getApiService(tokenManager))
                val viewModel: ReservationsViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ReservationsViewModel(repository) as T
                        }
                    }
                )
                ReservationsScreen(
                    viewModel = viewModel,
                    onBookingClick = { eventId ->
                        navController.navigate(Screen.EventDetail.createRoute(eventId))
                    }
                )
            }
            composable(Screen.Notifications.route) { NotificationsScreen() }
            composable(Screen.Profile.route) { 
                ProfileScreen(
                    onLogout = {
                        scope.launch {
                            tokenManager.deleteToken()
                            onLogout()
                        }
                    }
                ) 
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    EventSyncTheme {
        MainScreen(onLogout = {})
    }
}
