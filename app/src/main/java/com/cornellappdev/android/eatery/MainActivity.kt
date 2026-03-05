package com.cornellappdev.android.eatery

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
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.navigation.NavigationSetup
import com.cornellappdev.android.eatery.util.LockScreenOrientation
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var eateryRepository: EateryRepository

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the activity result launcher before onCreate
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            // Handle the result from the update flow
            if (result.resultCode != RESULT_OK) {
                Log.d("MainActivity", "Update flow failed! Result code: ${result.resultCode}")
            }
        }

        val hasOnboarded = runBlocking { userRepository.hasOnboarded() }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val typography = androidx.compose.material3.Typography()
        setContent {
            LockScreenOrientation()

            androidx.compose.material3.MaterialTheme(typography = typography) {
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
        runBlocking {
            configureTokens()
            userRepository.updateFavorites()
            userRepository.markTokensAsConfigured()
        }
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
                    popupSnackbarForCompleteUpdate()
                }
            }
    }

    private fun checkForUpdateAvailability() {
        // Check for updates
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Check if update is allowed based on priority and type
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
                    val listener = InstallStateUpdatedListener { state ->
                        when (state.installStatus()) {
                            InstallStatus.DOWNLOADING -> {
                                val bytesDownloaded = state.bytesDownloaded()
                                val totalBytesToDownload = state.totalBytesToDownload()
                                Log.d(
                                    "MainActivity",
                                    "Update downloading: $bytesDownloaded / $totalBytesToDownload"
                                )
                            }

                            InstallStatus.DOWNLOADED -> {
                                Log.d("MainActivity", "Update downloaded")
                                popupSnackbarForCompleteUpdate()
                            }

                            InstallStatus.INSTALLING -> {
                                Log.d("MainActivity", "Update installing")
                            }

                            InstallStatus.INSTALLED -> {
                                Log.d("MainActivity", "Update installed")
                            }

                            InstallStatus.FAILED -> {
                                Log.d("MainActivity", "Update failed")
                            }

                            InstallStatus.CANCELED -> {
                                Log.d("MainActivity", "Update canceled")
                            }

                            else -> {
                                Log.d("MainActivity", "Update status: ${state.installStatus()}")
                            }
                        }
                    }

                    // Register listener before starting flexible update
                    appUpdateManager.registerListener(listener)

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                }
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        // Show a snackbar or dialog to prompt user to complete the update
        // For Compose, you might want to show a dialog instead
        appUpdateManager.completeUpdate()
    }

    private suspend fun configureTokens() {
        if (!userRepository.hasLaunchedBefore()) {
            userRepository.registerDevice()
        }
        userRepository.getTokens()
    }
}
