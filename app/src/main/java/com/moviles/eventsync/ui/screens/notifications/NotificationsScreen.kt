package com.moviles.eventsync.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.eventsync.core.utils.DateTimeUtils
import com.moviles.eventsync.data.network.NotificationResponse

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel
) {
    val state by viewModel.state.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshNotifications() }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Notificaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Mantente al tanto de tus eventos",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when (val currentState = state) {
                is NotificationsState.Loading -> {
                    if (!isRefreshing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is NotificationsState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is NotificationsState.Success -> {
                    if (currentState.notifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No tienes notificaciones",
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(currentState.notifications) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onMarkAsRead = { viewModel.markAsRead(notification.id) }
                                )
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationResponse,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Indicador de no leído
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (notification.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = DateTimeUtils.formatEventDate(notification.sentDate), // Reutilizando formateador
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (!notification.isRead) {
                IconButton(onClick = onMarkAsRead) {
                    Icon(
                        Icons.Default.DoneAll,
                        contentDescription = "Marcar como leída",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
