package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.settings.SettingsOption
import com.appdev.eateryblueandroid.ui.components.settings.SwitchOption
import com.appdev.eateryblueandroid.ui.screens.SettingsLineSeparator
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun PrivacyScreen(profileViewModel: ProfileViewModel) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_leftarrow),
                contentDescription = null,
                tint = colorResource(id = R.color.black),
                modifier = Modifier
                    .clickable { onBack() }
                    .clip(CircleShape)

            )
        }

        Text(
            text = "Privacy",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Manage permissions and analytics",
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
        )
        Text(
            text = "Permissions",
            color = colorResource(id = R.color.black),
            textStyle = TextStyle.HEADER_H3
        )
        SettingsOption(
            title = "Location Access",
            description = "Used to find eateries near you",
            onClick = {},
            pointerIcon = painterResource(R.drawable.ic_upright_transfer_arrow)
        )
        SettingsLineSeparator()
        SettingsOption(
            title = "Notification Access",
            description = "Used to send device notifications",
            onClick = {},
            pointerIcon = painterResource(R.drawable.ic_upright_transfer_arrow)
        )
        SettingsLineSeparator()
        SettingsOption(
            title = "Notification Settings",
            onClick = { profileViewModel.transitionNotifications() },
            pointerIcon = painterResource(R.drawable.ic_chevron_right)
        )
        Text(
            text = "Analytics",
            color = colorResource(id = R.color.black),
            textStyle = TextStyle.HEADER_H3,
            modifier = Modifier.padding(top = 28.dp)
        )
        SwitchOption(
            title = "Share with Cornell AppDev",
            description = "Help us improve products and services",
            onCheckedChange = {}
        )
        SettingsLineSeparator()
        SettingsOption(
            title = "Privacy Policy",
            onClick = {},
            pointerIcon = painterResource(R.drawable.ic_upright_transfer_arrow)
        )
    }
}