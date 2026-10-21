package com.cornellappdev.android.eatery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cornellappdev.android.eatery.data.repositories.AuthTokenRepository
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.FavoriteItemSyncRepository
import com.cornellappdev.android.eatery.data.repositories.NotificationRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.navigation.NavigationSetup
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme
import com.cornellappdev.android.eatery.ui.theme.ColorTheme
import com.cornellappdev.android.eatery.ui.theme.rememberResolvedDarkMode
import com.cornellappdev.android.eatery.util.LockScreenOrientation
import com.cornellappdev.android.eatery.util.areNotificationsAllowedBySystem
import com.cornellappdev.android.eatery.util.firstOrOnReadFailure
import com.cornellappdev.android.eatery.util.isNotificationTap
import com.cornellappdev.android.eatery.util.needsNotificationPermissionRequest
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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

    @Inject
    lateinit var favoriteItemSyncRepository: FavoriteItemSyncRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(applicationContext) }
    private var flexibleUpdateListener: InstallStateUpdatedListener? = null

    /** Buffered so a tap that cold-starts the app is delivered once navigation is ready. */
    private val notificationHubRequests = Channel<Unit>(Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {}

        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) syncFcmTokenWithBackendIfAllowed()
        }

        val hasOnboarded = runBlocking { userRepository.hasOnboarded() }

        // Skipped on recreation (e.g. rotation) so the same tap doesn't reopen the hub.
        if (savedInstanceState == null && intent.isNotificationTap()) {
            notificationHubRequests.trySend(Unit)
        }

        enableEdgeToEdge()

        setContent {
            LockScreenOrientation()
            val resolvedDarkMode = rememberResolvedDarkMode()
            val activeMode = if (resolvedDarkMode) ColorTheme.darkMode else ColorTheme.lightMode


            AppColorTheme(activeMode) {
                NavigationSetup(
                    hasOnboarded = hasOnboarded,
                    notificationHubRequests = notificationHubRequests.receiveAsFlow()
                )
            }
        }
        val dataRefresher = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                eateryRepository.pingEateries()
                // Picks up notifications being turned on in system settings while away, and
                // retries anything that failed while the backend was unreachable or not deployed.
                if (authTokenRepository.tokensConfiguredFlow.value) {
                    syncFcmTokenWithBackendIfAllowed()
                    favoriteItemSyncRepository.syncPendingFavorites()
                }
            }
        }
        lifecycle.addObserver(dataRefresher)
        checkForUpdateAvailability()
        lifecycleScope.launch {
            // Waits for onboarding so a first-launch prompt doesn't cover the intro screens.
            userPreferencesRepository.hasOnboardedFlow.first { it }
            requestNotificationPermissionIfNeeded()
        }
        lifecycleScope.launch {
            configureTokens()
            syncFcmTokenWithBackendIfAllowed()
            userRepository.updateFavorites()
            favoriteItemSyncRepository.syncPendingFavorites()
            authTokenRepository.markTokensAsConfigured()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.isNotificationTap()) {
            notificationHubRequests.trySend(Unit)
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
        // Prompt at most once; the notification settings screen is the way back in after a denial.
        if (userPreferencesRepository.notificationPermissionRequestedFlow.firstOrOnReadFailure(true)) {
            return
        }

        val notificationsEnabled =
            userPreferencesRepository.notificationsEnabledFlow.firstOrOnReadFailure(false)
        if (notificationsEnabled && needsNotificationPermissionRequest(this)) {
            userPreferencesRepository.setNotificationPermissionRequested(true)
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun syncFcmTokenWithBackendIfAllowed() {
        if (!areNotificationsAllowedBySystem(this)) return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            if (token.isNullOrBlank()) {
                return@addOnCompleteListener
            }

            lifecycleScope.launch { notificationRepository.registerFcmToken(token) }
        }
    }
}
