package com.cornellappdev.android.eatery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
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
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Privacy",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Manage permissions and analytics",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
        )
        Text(
            text = "Permissions",
            color = Color.Black,
            style = EateryBlueTypography.h4,
        )
        SettingsOption(
            title = "Location Access",
            description = "Used to find eateries near you",
            onClick = {
                startActivity(
                    context,
                    Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    ),
                    null
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
            title = "Notification Access",
            description = "Used to send device notifications",
            onClick = {
                val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)

                startActivity(context, intent, null)
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
            text = "Analytics",
            color = Color.Black,
            style = EateryBlueTypography.h4,
            modifier = Modifier.padding(top = 28.dp)
        )
        SwitchOption(
            title = "Share with Cornell AppDev",
            description = "Help us improve products and services",
            initialValue = !privacyViewModel.analyticsDisabled,
            onCheckedChange = { switched ->
                privacyViewModel.setAnalyticsDisabled(switched)
                FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!switched)
            }
        )
        SettingsLineSeparator()

        SettingsOption(
            title = "Privacy Policy",
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
