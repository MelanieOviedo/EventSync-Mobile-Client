package com.moviles.eventsync.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.eventsync.data.network.RetrofitClient
import com.moviles.eventsync.data.repository.EventsRepository
import com.moviles.eventsync.navigation.Screen
import com.moviles.eventsync.ui.components.EventSyncBottomBar
import com.moviles.eventsync.ui.screens.events.EventsScreen
import com.moviles.eventsync.ui.screens.events.EventsViewModel
import com.moviles.eventsync.ui.screens.notifications.NotificationsScreen
import com.moviles.eventsync.ui.screens.profile.ProfileScreen
import com.moviles.eventsync.ui.screens.reservations.ReservationsScreen
import com.moviles.eventsync.ui.theme.EventSyncTheme

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    
    Scaffold(
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
                val repository = EventsRepository(RetrofitClient.apiService)
                val viewModel: EventsViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return EventsViewModel(repository) as T
                        }
                    }
                )
                EventsScreen(viewModel = viewModel)
            }
            composable(Screen.Reservations.route) { ReservationsScreen() }
            composable(Screen.Notifications.route) { NotificationsScreen() }
            composable(Screen.Profile.route) { ProfileScreen(onLogout) }
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
