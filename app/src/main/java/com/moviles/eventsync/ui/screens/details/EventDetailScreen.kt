package com.moviles.eventsync.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moviles.eventsync.core.AppConstants
import com.moviles.eventsync.core.utils.DateTimeUtils
import com.moviles.eventsync.ui.components.EventSyncButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Int,
    viewModel: EventDetailViewModel,
    onBack: () -> Unit,
    onReservationSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val reservationState by viewModel.reservationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(eventId) {
        viewModel.getEventById(eventId)
    }

    // Manejar el resultado de la reserva
    LaunchedEffect(reservationState) {
        when (val res = reservationState) {
            is ReservationState.Success -> {
                viewModel.resetReservationState()
                onReservationSuccess(res.message)
                onBack() // Cerrar la vista de detalles
            }
            is ReservationState.Error -> {
                snackbarHostState.showSnackbar(res.message)
                viewModel.resetReservationState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val currentState = state) {
                is EventDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EventDetailState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is EventDetailState.Success -> {
                    val event = currentState.event
                    val fullImageUrl = event.imagePath?.let { path ->
                        val normalized = path.replace("\\", "/")
                        val cleanPath = normalized.removePrefix("wwwroot/").removePrefix("/")
                        val baseUrl = AppConstants.BASE_URL.removeSuffix("/")
                        if (cleanPath.startsWith("http")) cleanPath else "$baseUrl/$cleanPath"
                    } ?: "https://via.placeholder.com/800x400?text=Sin+Imagen"

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = fullImageUrl,
                            contentDescription = event.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = event.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DateTimeUtils.formatEventDate(event.date),
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Cupos disponibles: ${event.availableSpots} / ${event.maxCapacity}", fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Acerca de este evento",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = event.description,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            EventSyncButton(
                                text = "Reservar mi lugar",
                                onClick = { viewModel.makeReservation(event.id) },
                                enabled = event.availableSpots > 0,
                                isLoading = reservationState is ReservationState.Loading
                            )
                        }
                    }
                }
            }
        }
    }
}
