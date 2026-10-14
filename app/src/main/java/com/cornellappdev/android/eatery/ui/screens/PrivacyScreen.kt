package com.cornellappdev.android.eatery.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.LargeTitleHeader
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.PrivacyViewModel
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.areNotificationsAllowedBySystem
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    privacyViewModel: PrivacyViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uriCurrent = LocalUriHandler.current

    // Re-read on resume, since the user may have changed these in system settings.
    var locationAllowed by remember { mutableStateOf(isLocationAllowed(context)) }
    var notificationsAllowed by remember { mutableStateOf(areNotificationsAllowedBySystem(context)) }
    LifecycleResumeEffect(Unit) {
        locationAllowed = isLocationAllowed(context)
        notificationsAllowed = areNotificationsAllowedBySystem(context)
        onPauseOrDispose { }
    }

    PrivacyScreenContent(
        analyticsDisabled = privacyViewModel.analyticsDisabled,
        locationAllowed = locationAllowed,
        notificationsAllowed = notificationsAllowed,
        onBackClick = onBackClick,
        onOpenLocationSettings = {
            context.startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
            )
        },
        onOpenNotificationSettings = {
            val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            context.startActivity(intent)
        },
        onNotificationSettingsClick = onNotificationSettingsClick,
        onAnalyticsDisabledChange = { disabled ->
            privacyViewModel.setAnalyticsDisabled(disabled)
            FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!disabled)
        },
        onOpenPrivacyPolicy = { uriCurrent.openUri("https://www.cornellappdev.com/privacy") }
    )
}

private fun isLocationAllowed(context: Context): Boolean =
    listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

@Composable
private fun PrivacyScreenContent(
    analyticsDisabled: Boolean,
    locationAllowed: Boolean,
    notificationsAllowed: Boolean,
    onBackClick: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onAnalyticsDisabledChange: (Boolean) -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
            .fillMaxSize()
    ) {
        LargeTitleHeader(
            title = stringResource(R.string.privacy_title),
            subtitle = stringResource(R.string.privacy_description),
            onBackClick = onBackClick
        )
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
            Text(
                text = stringResource(R.string.privacy_permissions_heading),
                color = currentColors.textPrimary,
                style = EateryBlueTypography.h5,
            )
            SettingsOption(
                title = stringResource(R.string.privacy_location_access_title),
                description = stringResource(R.string.privacy_location_access_description),
                onClick = onOpenLocationSettings,
                compact = true,
                trailingIcon = { PermissionStatus(allowed = locationAllowed) }
            )
            SettingsLineSeparator()
            SettingsOption(
                title = stringResource(R.string.privacy_notification_access_title),
                description = stringResource(R.string.privacy_notification_access_description),
                onClick = onOpenNotificationSettings,
                compact = true,
                trailingIcon = { PermissionStatus(allowed = notificationsAllowed) }
            )
            SettingsLineSeparator()
            SettingsOption(
                title = stringResource(R.string.privacy_notification_settings_title),
                onClick = onNotificationSettingsClick,
                compact = true,
                trailingIcon = { BrandIcon(R.drawable.ic_chevron_right) }
            )

            Text(
                text = stringResource(R.string.privacy_analytics_heading),
                color = currentColors.textPrimary,
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(top = 24.dp)
            )
            SwitchOption(
                title = stringResource(R.string.privacy_share_with_cornell_appdev_title),
                description = stringResource(R.string.privacy_share_with_cornell_appdev_description),
                initialValue = !analyticsDisabled,
                onCheckedChange = { shareEnabled -> onAnalyticsDisabledChange(!shareEnabled) }
            )
            SettingsLineSeparator()
            SettingsOption(
                title = stringResource(R.string.privacy_policy_title),
                onClick = onOpenPrivacyPolicy,
                compact = true,
                trailingIcon = { BrandIcon(R.drawable.ic_external_link) }
            )
        }
    }
}

/**
 * "Allowed ↗" / "Not Allowed ↗" link that sends the user to system settings for a permission.
 */
@Composable
private fun PermissionStatus(allowed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = stringResource(
                if (allowed) R.string.privacy_permission_allowed
                else R.string.privacy_permission_not_allowed
            ),
            color = currentColors.contentBrand,
            style = EateryBlueTypography.button
        )
        BrandIcon(R.drawable.ic_external_link)
    }
}

@Composable
private fun BrandIcon(@DrawableRes id: Int) {
    Icon(
        painter = painterResource(id),
        contentDescription = null,
        tint = currentColors.contentBrand,
    )
}

@DualModePreview
@Composable
private fun PrivacyScreenPreview() = EateryPreview {
    PrivacyScreenContent(
        analyticsDisabled = false,
        locationAllowed = true,
        notificationsAllowed = true,
        onBackClick = {},
        onOpenLocationSettings = {},
        onOpenNotificationSettings = {},
        onNotificationSettingsClick = {},
        onAnalyticsDisabledChange = {},
        onOpenPrivacyPolicy = {}
    )
}
