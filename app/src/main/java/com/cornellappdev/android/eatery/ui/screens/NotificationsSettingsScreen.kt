package com.cornellappdev.android.eatery.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.LargeTitleHeader
import com.cornellappdev.android.eatery.ui.components.settings.PillShape
import com.cornellappdev.android.eatery.ui.components.settings.SettingsCard
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.NotificationsSettingsViewModel
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.needsNotificationPermissionRequest
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("InlinedApi")
@Composable
fun NotificationsSettingsScreen(
    onBackClick: () -> Unit,
    notificationsSettingsViewModel: NotificationsSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by notificationsSettingsViewModel.uiState.collectAsStateWithLifecycle()

    var pendingEnablePermissionRequest by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        notificationsSettingsViewModel.syncErrorFlow.collect { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    val syncAllNotificationsWithBackend = remember(notificationsSettingsViewModel) {
        { enabled: Boolean ->
            // enable first so that UI updates immediately, then sync with backend
            notificationsSettingsViewModel.setAllNotificationsEnabled(enabled)

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                val token = if (task.isSuccessful) {
                    task.result
                } else {
                    Log.w("NotificationsSettings", "Failed to fetch FCM token", task.exception)
                    null
                }
                notificationsSettingsViewModel.syncNotificationSettingsWithBackend(enabled, token)
            }
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingEnablePermissionRequest) {
            syncAllNotificationsWithBackend(true)
        }
        pendingEnablePermissionRequest = false
    }

    Column(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
            .fillMaxSize()
    ) {
        LargeTitleHeader(
            title = stringResource(R.string.notifications_title),
            subtitle = stringResource(R.string.notifications_description),
            onBackClick = onBackClick
        )

        SnackbarHost(hostState = snackbarHostState)

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard(shape = PillShape) {
                SwitchOption(
                    title = stringResource(R.string.notifications_pause_all_title),
                    description = "",
                    checked = !uiState.allNotificationsEnabled,
                    onCheckedChange = { isPaused ->
                        val isEnabled = !isPaused
                        if (
                            isEnabled &&
                            needsNotificationPermissionRequest(context)
                        ) {
                            pendingEnablePermissionRequest = true
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            return@SwitchOption
                        }

                        pendingEnablePermissionRequest = false
                        syncAllNotificationsWithBackend(isEnabled)
                    }
                )
            }

            if (uiState.allNotificationsEnabled) {
                SettingsCard {
                    SwitchOption(
                        title = stringResource(R.string.notifications_favorite_item_title),
                        description = stringResource(R.string.notifications_favorite_item_description),
                        checked = uiState.favoriteItemNotificationsEnabled,
                        onCheckedChange = notificationsSettingsViewModel::setFavoriteItemNotificationsEnabled
                    )
                    SettingsLineSeparator()
                    SwitchOption(
                        title = stringResource(R.string.notifications_cornell_appdev_title),
                        description = stringResource(R.string.notifications_cornell_appdev_description),
                        checked = uiState.cornellAppdevNotificationsEnabled,
                        onCheckedChange = notificationsSettingsViewModel::setCornellAppdevNotificationsEnabled
                    )
                }
            }
        }
    }
}

@DualModePreview
@Composable
private fun NotificationsSettingsScreenPreview() = EateryPreview {
    NotificationsSettingsScreen(onBackClick = {})
}
