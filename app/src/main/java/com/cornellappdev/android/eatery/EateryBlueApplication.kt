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

	private fun createDefaultNotificationChannel() {
		val notificationManager =
			getSystemService(NotificationManager::class.java) as NotificationManager
		val defaultChannel = NotificationChannel(
			getString(R.string.fcm_default_channel_id),
			getString(R.string.fcm_default_channel_name),
			NotificationManager.IMPORTANCE_DEFAULT
		)
		notificationManager.createNotificationChannel(defaultChannel)
	}
}
