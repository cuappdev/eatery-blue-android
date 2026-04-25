package com.cornellappdev.android.eatery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.PrivacyViewModel
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun PrivacyScreen(privacyViewModel: PrivacyViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uriCurrent = LocalUriHandler.current

    PrivacyScreenContent(
        analyticsDisabled = privacyViewModel.analyticsDisabled,
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
        onAnalyticsPreferenceChange = { switched ->
            privacyViewModel.setAnalyticsDisabled(switched)
            FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!switched)
        },
        onOpenPrivacyPolicy = { uriCurrent.openUri("https://www.cornellappdev.com/privacy") }
    )
}

@Composable
private fun PrivacyScreenContent(
    analyticsDisabled: Boolean,
    onOpenLocationSettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onAnalyticsPreferenceChange: (Boolean) -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.privacy_title),
            color = currentColors.textPrimary,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = stringResource(R.string.privacy_description),
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = currentColors.textPrimary,
            modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
        )
        Text(
            text = stringResource(R.string.privacy_permissions_heading),
            color = currentColors.textPrimary,
            style = EateryBlueTypography.h4,
        )
        SettingsOption(
            title = stringResource(R.string.privacy_location_access_title),
            description = stringResource(R.string.privacy_location_access_description),
            onClick = onOpenLocationSettings,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = currentColors.textPrimary,
                )
            })
        SettingsLineSeparator()
        SettingsOption(
            title = stringResource(R.string.privacy_notification_access_title),
            description = stringResource(R.string.privacy_notification_access_description),
            onClick = onOpenNotificationSettings,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = currentColors.textPrimary,
                )
            }
        )
        SettingsLineSeparator()
        Text(
            text = stringResource(R.string.privacy_analytics_heading),
            color = currentColors.textPrimary,
            style = EateryBlueTypography.h4,
            modifier = Modifier.padding(top = 28.dp)
        )
        SwitchOption(
            title = stringResource(R.string.privacy_share_with_cornell_appdev_title),
            description = stringResource(R.string.privacy_share_with_cornell_appdev_description),
            initialValue = !analyticsDisabled,
            onCheckedChange = onAnalyticsPreferenceChange
        )
        SettingsLineSeparator()

        SettingsOption(
            title = stringResource(R.string.privacy_policy_title),
            onClick = onOpenPrivacyPolicy,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = currentColors.textPrimary,
                )
            }
        )
    }
}

@DualModePreview
@Composable
private fun PrivacyScreenPreview() = EateryPreview {
    PrivacyScreenContent(
        analyticsDisabled = false,
        onOpenLocationSettings = {},
        onOpenNotificationSettings = {},
        onAnalyticsPreferenceChange = {},
        onOpenPrivacyPolicy = {}
    )
}

