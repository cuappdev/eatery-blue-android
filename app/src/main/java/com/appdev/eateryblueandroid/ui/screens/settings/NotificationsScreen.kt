package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
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
import com.appdev.eateryblueandroid.util.NotificationsSettingsType
import com.appdev.eateryblueandroid.util.notificationSettingsMap
import com.appdev.eateryblueandroid.util.saveNotificationSetting

@Composable
fun NotificationsScreen(profileViewModel: ProfileViewModel) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    var pausedNotifications by remember { mutableStateOf(notificationSettingsMap[NotificationsSettingsType.PAUSED]!!) }

    val interactionSource = MutableInteractionSource()
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
                    .clickable(
                        onClick = { onBack() },
                        interactionSource = interactionSource,
                        indication = null
                    )
                    .clip(CircleShape)

            )
        }

        Text(
            text = "Notifications",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Manage item and promotional notifications",
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SwitchOption(
            title = "Pause All Notifications",
            description = "",
            enabled = true,
            initialValue = notificationSettingsMap[NotificationsSettingsType.PAUSED]!!,
            onCheckedChange = { switched ->
                pausedNotifications = switched
                saveNotificationSetting(NotificationsSettingsType.PAUSED, switched)
            })
        SettingsLineSeparator()
        SwitchOption(
            title = "Favorite Item Notifications",
            description = "Get notified when favorite items are served",
            enabled = !pausedNotifications,
            initialValue = notificationSettingsMap[NotificationsSettingsType.FAVORITE_ITEMS]!!,
            onCheckedChange = { switched ->
                saveNotificationSetting(NotificationsSettingsType.FAVORITE_ITEMS, switched)
            }
        )
        SettingsLineSeparator()
        SwitchOption(
            title = "Cornell AppDev Notifications",
            description = "Get notified about new releases and feedback",
            enabled = !pausedNotifications,
            initialValue = notificationSettingsMap[NotificationsSettingsType.APPDEV]!!,
            onCheckedChange = { switched ->
                saveNotificationSetting(NotificationsSettingsType.APPDEV, switched)
            }
        )
        SettingsLineSeparator()
        SwitchOption(
            title = "Cornell Dining Notifications",
            description = "Get notified about special menus and meals",
            enabled = !pausedNotifications,
            initialValue = notificationSettingsMap[NotificationsSettingsType.DINING]!!,
            onCheckedChange = { switched ->
                saveNotificationSetting(NotificationsSettingsType.DINING, switched)
            }
        )
        SettingsLineSeparator()
        SwitchOption(
            title = "Account Notifications",
            description = "Get notified about account security and privacy",
            enabled = !pausedNotifications,
            initialValue = notificationSettingsMap[NotificationsSettingsType.ACCOUNT]!!,
            onCheckedChange = { switched ->
                saveNotificationSetting(NotificationsSettingsType.ACCOUNT, switched)
            }
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = null,
            title = "Privacy Settings",
            description = "",
            onClick = { profileViewModel.transitionPrivacy() }
        )
    }

    BackHandler {
        onBack()
    }
}