package com.cornellappdev.android.eatery

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.repositories.AuthTokenRepository
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.navigation.NavigationSetup
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTheme
import com.cornellappdev.android.eatery.util.LockScreenOrientation
import com.cornellappdev.android.eatery.util.canGetNotifications
import com.cornellappdev.android.eatery.util.shouldRequestNotificationPermission
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val LOG_TAG = "MainActivity"
    }

    @Inject
    lateinit var eateryRepository: EateryRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var authTokenRepository: AuthTokenRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(applicationContext) }
    private var flexibleUpdateListener: InstallStateUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {}

        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                lifecycleScope.launch {
                    syncFcmTokenWithBackendIfAllowed()
                }
            }
        }

        val hasOnboarded = runBlocking { userRepository.hasOnboarded() }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            LockScreenOrientation()

            EateryBlueTheme {
                NavigationSetup(hasOnboarded)
            }
        }
        val dataRefresher = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                eateryRepository.pingEateries()
            }
        }
        lifecycle.addObserver(dataRefresher)
        checkForUpdateAvailability()
        lifecycleScope.launch {
            configureTokens()
            requestNotificationPermissionIfNeeded()
            syncFcmTokenWithBackendIfAllowed()
            userRepository.updateFavorites()
            authTokenRepository.markTokensAsConfigured()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterFlexibleUpdateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        // in case onStop cleanup did not run
        unregisterFlexibleUpdateListener()
    }

    override fun onResume() {
        super.onResume()

        // Check if there's an update that's already downloaded and waiting to be installed
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }

                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    // If the update is downloaded but not installed, notify the user to complete the update.
                    appUpdateManager.completeUpdate()
                }
            }
    }

    private fun checkForUpdateAvailability() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                val isImmediateUpdateAllowed =
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                val isFlexibleUpdateAllowed =
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

                // For high priority updates, use immediate update
                if (appUpdateInfo.updatePriority() >= 4 && isImmediateUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
                // For normal priority updates, use flexible update
                else if (isFlexibleUpdateAllowed) {
                    // Create a listener to track flexible update progress
                    flexibleUpdateListener = InstallStateUpdatedListener { state ->
                        if (state.installStatus() == InstallStatus.DOWNLOADED) {
                            unregisterFlexibleUpdateListener()
                            appUpdateManager.completeUpdate()
                        }
                    }
                    flexibleUpdateListener?.let { appUpdateManager.registerListener(it) }

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                }
            }
        }
    }

    private fun unregisterFlexibleUpdateListener() {
        flexibleUpdateListener?.let(appUpdateManager::unregisterListener)
        flexibleUpdateListener = null
    }

    private suspend fun configureTokens() {
        authTokenRepository.getTokens()
    }

    @SuppressLint("InlinedApi")
    private suspend fun requestNotificationPermissionIfNeeded() {
        if (shouldRequestNotificationPermission(
                this,
                userPreferencesRepository.notificationsEnabledFlow
            )
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private suspend fun syncFcmTokenWithBackendIfAllowed() {
        if (!canGetNotifications(this, userPreferencesRepository.notificationsEnabledFlow)) {
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(LOG_TAG, "Fetched FCM registration token: $token")
            if (token.isNullOrBlank()) {
                return@addOnCompleteListener
            }

            lifecycleScope.launch {
                when (val result = userRepository.enableNotifications(token)) {
                    is Result.Success -> Unit
                    is Result.Error -> Log.w(
                        LOG_TAG,
                        "Failed to register FCM token: ${result.error}"
                    )
                }
            }
        }
    }
}
