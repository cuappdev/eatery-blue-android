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

    /** High importance so pushes show as heads-up banners. */
    private fun createDefaultNotificationChannel() {
        val channel = NotificationChannel(
            getString(R.string.fcm_default_channel_id),
            getString(R.string.fcm_default_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
