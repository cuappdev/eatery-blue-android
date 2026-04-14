package com.cornellappdev.android.eatery.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
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
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GraySix
import com.cornellappdev.android.eatery.ui.viewmodels.PrivacyViewModel
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun PrivacyScreen(privacyViewModel: PrivacyViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uriCurrent = LocalUriHandler.current

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .then(Modifier.statusBarsPadding())
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.privacy_title),
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = stringResource(R.string.privacy_description),
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
        )
        Text(
            text = stringResource(R.string.privacy_permissions_heading),
            color = Color.Black,
            style = EateryBlueTypography.h4,
        )
        SettingsOption(
            title = stringResource(R.string.privacy_location_access_title),
            description = stringResource(R.string.privacy_location_access_description),
            onClick = {
                context.startActivity(
                    Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = EateryBlue,
                )
            })
        SettingsLineSeparator()
        SettingsOption(
            title = stringResource(R.string.privacy_notification_access_title),
            description = stringResource(R.string.privacy_notification_access_description),
            onClick = {
                val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)

                context.startActivity(intent)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = EateryBlue,
                )
            }
        )
        SettingsLineSeparator()
        Text(
            text = stringResource(R.string.privacy_analytics_heading),
            color = Color.Black,
            style = EateryBlueTypography.h4,
            modifier = Modifier.padding(top = 28.dp)
        )
        SwitchOption(
            title = stringResource(R.string.privacy_share_with_cornell_appdev_title),
            description = stringResource(R.string.privacy_share_with_cornell_appdev_description),
            initialValue = !privacyViewModel.analyticsDisabled,
            onCheckedChange = { switched ->
                privacyViewModel.setAnalyticsDisabled(switched)
                FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!switched)
            }
        )
        SettingsLineSeparator()

        SettingsOption(
            title = stringResource(R.string.privacy_policy_title),
            onClick = { uriCurrent.openUri("https://www.cornellappdev.com/privacy") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = EateryBlue,
                )
            }
        )
    }
}
