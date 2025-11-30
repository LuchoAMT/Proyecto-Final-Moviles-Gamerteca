package com.ucb.proyectofinalgamerteca.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ucb.proyectofinalgamerteca.MainActivity
import com.ucb.proyectofinalgamerteca.R

class FirebaseNotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "Mensaje recibido: ${message.from}")

        // Si la notificación viene con título y cuerpo, la mostramos manualmente
        // para que el usuario la vea aunque esté usando la app en ese momento.
        message.notification?.let {
            sendNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo Token generado (no se guardará): $token")
    }

    // Función auxiliar para construir y mostrar la notificación visual
    private fun sendNotification(title: String?, messageBody: String?) {
        // Al tocar la notificación, abrimos la MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "gamerteca_global_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            // IMPORTANTE: Asegúrate de tener un icono pequeño (blanco y transparente)
            // Si no tienes uno, usa el de launcher por ahora, pero lo ideal es un icono monocromático.
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title ?: "Gamerteca")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Configuración necesaria para Android 8.0+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Avisos Gamerteca", // Nombre visible para el usuario en ajustes
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}