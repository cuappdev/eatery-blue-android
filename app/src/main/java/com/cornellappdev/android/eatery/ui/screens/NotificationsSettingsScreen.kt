package com.cornellappdev.android.eatery.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.NotificationsSettingsViewModel
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.needsNotificationPermissionRequest
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("InlinedApi")
@Composable
fun NotificationsSettingsScreen(
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
                val token = task.result
                if (!task.isSuccessful) {
                    Log.w("NotificationsSettings", "Failed to fetch FCM token", task.exception)
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
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
            .fillMaxSize()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.notifications_title),
            color = currentColors.contentBrand,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )

        Text(
            text = stringResource(R.string.notifications_description),
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = currentColors.textPrimary,
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SwitchOption(
            title = stringResource(R.string.notifications_all_title),
            description = "",
            checked = uiState.allNotificationsEnabled,
            onCheckedChange = { isEnabled ->
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

        if (uiState.allNotificationsEnabled) {
            Spacer(modifier = Modifier.height(12.dp))

            SwitchOption(
                title = stringResource(R.string.notifications_favorite_item_title),
                description = stringResource(R.string.notifications_favorite_item_description),
                checked = uiState.favoriteItemNotificationsEnabled,
                onCheckedChange = {
                    notificationsSettingsViewModel.setFavoriteItemNotificationsEnabled(it)
                }
            )

            SwitchOption(
                title = stringResource(R.string.notifications_favorite_eatery_open_title),
                description = stringResource(R.string.notifications_favorite_eatery_open_description),
                checked = uiState.favoriteEateryOpeningNotificationsEnabled,
                onCheckedChange = {
                    notificationsSettingsViewModel.setFavoriteEateryOpeningNotificationsEnabled(it)
                }
            )

            SwitchOption(
                title = stringResource(R.string.notifications_favorite_eatery_close_title),
                description = stringResource(R.string.notifications_favorite_eatery_close_description),
                checked = uiState.favoriteEateryClosingNotificationsEnabled,
                onCheckedChange = {
                    notificationsSettingsViewModel.setFavoriteEateryClosingNotificationsEnabled(it)
                }
            )
        }
    }
}

@DualModePreview
@Composable
private fun NotificationsSettingsScreenPreview() = EateryPreview {
    NotificationsSettingsScreen()
}
