package com.cornellappdev.android.eatery.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.LargeTitleHeader
import com.cornellappdev.android.eatery.ui.components.settings.SettingsCard
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.NotificationsSettingsViewModel
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.areNotificationsAllowedBySystem
import com.cornellappdev.android.eatery.util.needsNotificationPermissionRequest

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

    val syncErrorMessage = stringResource(R.string.notifications_sync_error)
    LaunchedEffect(Unit) {
        notificationsSettingsViewModel.syncErrorFlow.collect {
            snackbarHostState.showSnackbar(syncErrorMessage)
        }
    }

    var systemNotificationsAllowed by remember {
        mutableStateOf(areNotificationsAllowedBySystem(context))
    }

    // Recheck on resume so returning from system settings, or changes made on another device,
    // are reflected without reopening the screen.
    LifecycleResumeEffect(Unit) {
        systemNotificationsAllowed = areNotificationsAllowedBySystem(context)
        notificationsSettingsViewModel.loadSettingsFromBackend()
        onPauseOrDispose { }
    }

    // Before the first prompt on API 33+ the permission isn't granted, but the user hasn't turned
    // notifications off either, so unpausing should still prompt rather than point to Settings.
    val systemNotificationsDenied = !systemNotificationsAllowed && (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || uiState.permissionRequested
            )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        systemNotificationsAllowed = areNotificationsAllowedBySystem(context)
        if (granted && pendingEnablePermissionRequest) {
            notificationsSettingsViewModel.setAllNotificationsEnabled(true)
        }
        pendingEnablePermissionRequest = false
    }

    Box(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
            .fillMaxSize()
    ) {
        Column {
            LargeTitleHeader(
                title = stringResource(R.string.notifications_title),
                subtitle = stringResource(R.string.notifications_description),
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (systemNotificationsDenied) {
                    SystemNotificationsOffCard(
                        onOpenSettingsClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        }
                    )
                }

                SettingsCard(shape = CircleShape) {
                    SwitchOption(
                        title = stringResource(R.string.notifications_pause_all_title),
                        checked = !uiState.allNotificationsEnabled,
                        enabled = !systemNotificationsDenied,
                        onCheckedChange = { isPaused ->
                            val isEnabled = !isPaused
                            if (
                                isEnabled &&
                                needsNotificationPermissionRequest(context)
                            ) {
                                pendingEnablePermissionRequest = true
                                notificationsSettingsViewModel.markPermissionRequested()
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                return@SwitchOption
                            }

                            pendingEnablePermissionRequest = false
                            notificationsSettingsViewModel.setAllNotificationsEnabled(isEnabled)
                        }
                    )
                }

                if (uiState.allNotificationsEnabled) {
                    SettingsCard {
                        SwitchOption(
                            title = stringResource(R.string.notifications_favorite_item_title),
                            description = stringResource(R.string.notifications_favorite_item_description),
                            checked = uiState.favoriteItemNotificationsEnabled,
                            enabled = !systemNotificationsDenied,
                            onCheckedChange = notificationsSettingsViewModel::setFavoriteItemNotificationsEnabled
                        )
                        SettingsLineSeparator()
                        SwitchOption(
                            title = stringResource(R.string.notifications_cornell_appdev_title),
                            description = stringResource(R.string.notifications_cornell_appdev_description),
                            checked = uiState.cornellAppdevNotificationsEnabled,
                            enabled = !systemNotificationsDenied,
                            onCheckedChange = notificationsSettingsViewModel::setCornellAppdevNotificationsEnabled
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun SystemNotificationsOffCard(onOpenSettingsClick: () -> Unit) {
    SettingsCard {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                text = stringResource(R.string.notifications_system_off_title),
                style = EateryBlueTypography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                color = currentColors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.notifications_system_off_description),
                style = EateryBlueTypography.body2,
                color = currentColors.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenSettingsClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = currentColors.accentPrimary)
            ) {
                Text(
                    text = stringResource(R.string.notifications_open_settings),
                    color = currentColors.textPrimary,
                    style = EateryBlueTypography.button
                )
            }
        }
    }
}

@DualModePreview
@Composable
private fun SystemNotificationsOffCardPreview() = EateryPreview {
    SystemNotificationsOffCard(onOpenSettingsClick = {})
}

@DualModePreview
@Composable
private fun NotificationsSettingsScreenPreview() = EateryPreview {
    NotificationsSettingsScreen(onBackClick = {})
}
