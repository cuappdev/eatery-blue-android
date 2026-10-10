package com.cornellappdev.android.eatery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

/**
 * This class is necessary to enable Hilt dependency injection. Can leave it empty as so.
 */
@HiltAndroidApp
class EateryBlueApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		createDefaultNotificationChannel()
	}

	/**
	 * High importance so pushes show as heads-up banners. A channel's importance can't be raised
	 * after creation, so this uses a new channel ID and deletes the old default-importance one.
	 */
	private fun createDefaultNotificationChannel() {
		val notificationManager =
			getSystemService(NotificationManager::class.java) as NotificationManager
		notificationManager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
		val defaultChannel = NotificationChannel(
			getString(R.string.fcm_default_channel_id),
			getString(R.string.fcm_default_channel_name),
			NotificationManager.IMPORTANCE_HIGH
		)
		notificationManager.createNotificationChannel(defaultChannel)
	}

	private companion object {
		const val LEGACY_CHANNEL_ID = "eatery_notifications"
	}
}
