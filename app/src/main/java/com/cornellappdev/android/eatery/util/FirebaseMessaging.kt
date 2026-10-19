package com.cornellappdev.android.eatery.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cornellappdev.android.eatery.MainActivity
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.repositories.NotificationRepository
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

/**
 * Whether this intent came from tapping a push notification, either one built by this service
 * or one the system displayed while the app was backgrounded.
 */
fun Intent.isNotificationTap(): Boolean =
    action == FirebaseMessaging.BACKEND_CLICK_ACTION ||
            getBooleanExtra(FirebaseMessaging.EXTRA_OPEN_NOTIFICATION_HUB, false) ||
            extras?.containsKey("google.message_id") == true

@AndroidEntryPoint
class FirebaseMessaging : FirebaseMessagingService() {
    @Inject
    lateinit var notificationRepository: NotificationRepository

    companion object {
        const val LOG_TAG = "FirebaseMessaging"
        private const val DEFAULT_TITLE = "Eatery Blue"

        /** Marks intents from notifications this service builds while the app is foregrounded. */
        const val EXTRA_OPEN_NOTIFICATION_HUB = "open_notification_hub"

        /** Click action the backend sets on pushes, used when the system displays them. */
        const val BACKEND_CLICK_ACTION = "FLUTTER_NOTIFICATION_CLICK"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(LOG_TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(LOG_TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Notification payloads only reach here while the app is foregrounded; data-only
        // payloads always do.
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"]
        if (!body.isNullOrBlank()) {
            sendNotification(messageTitle = title ?: DEFAULT_TITLE, messageBody = body)
        }
    }

    override fun onNewToken(token: String) {
        serviceScope.launch { notificationRepository.registerFcmToken(token) }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun sendNotification(messageTitle: String, messageBody: String) {
        if (!runBlocking { notificationRepository.canShowNotifications() }) return

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(EXTRA_OPEN_NOTIFICATION_HUB, true)
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
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
