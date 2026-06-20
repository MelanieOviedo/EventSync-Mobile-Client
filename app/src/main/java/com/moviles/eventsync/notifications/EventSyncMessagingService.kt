package com.moviles.eventsync.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moviles.eventsync.MainActivity
import com.moviles.eventsync.R
import com.moviles.eventsync.data.TokenManager
import com.moviles.eventsync.data.network.EventSyncApi
import com.moviles.eventsync.data.network.FcmTokenRequest
import com.moviles.eventsync.data.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first

class EventSyncMessagingService : FirebaseMessagingService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val tokenManager = TokenManager(applicationContext)
        
        // Solo intentamos actualizar si el usuario ya está logueado
        scope.launch {
            tokenManager.token.first()?.let {
                try {
                    val api = RetrofitClient.getApiService(tokenManager)
                    api.updateFcmToken(FcmTokenRequest(token))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val title = message.notification?.title ?: message.data["title"] ?: "Nueva notificación"
        val body = message.notification?.body ?: message.data["body"] ?: "Tienes una actualización de EventSync"
        
        sendNotification(title, body)
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "event_sync_notifications"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de que este recurso exista
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "EventSync Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
