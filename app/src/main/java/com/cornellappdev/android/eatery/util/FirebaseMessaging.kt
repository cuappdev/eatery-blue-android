package com.cornellappdev.android.eatery.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cornellappdev.android.eatery.MainActivity
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessaging : FirebaseMessagingService() {
    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    companion object {
        const val LOG_TAG = "FirebaseMessaging"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(LOG_TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(LOG_TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(LOG_TAG, "Message Notification Body: ${it.body}")

            if (it.body != null) {
                sendNotification(messageTitle = it.title ?: "Eatery Blue", messageBody = it.body!!)
            }
        }

        if (remoteMessage.notification == null && remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Eatery Blue"
            val body = remoteMessage.data["body"]
            if (!body.isNullOrBlank()) {
                sendNotification(messageTitle = title, messageBody = body)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "Refreshed token: $token")

        serviceScope.launch {
            if (!canGetNotifications(
                    this@FirebaseMessaging,
                    userPreferencesRepository.notificationsEnabledFlow
                )
            ) {
                return@launch
            }

            when (val result = userRepository.enableNotifications(token)) {
                is Result.Success -> Unit
                is Result.Error -> Log.w(
                    LOG_TAG,
                    "Failed to sync refreshed FCM token: ${result.error}"
                )
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun sendNotification(messageTitle: String, messageBody: String) {
        if (!runBlocking {
                canGetNotifications(
                    this@FirebaseMessaging,
                    userPreferencesRepository.notificationsEnabledFlow
                )
            }) {
            Log.d(
                LOG_TAG,
                "Skipping local notification because notifications are disabled or permission is missing"
            )
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.fcm_default_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_eaterylogo_blue)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            getString(R.string.fcm_default_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

}
