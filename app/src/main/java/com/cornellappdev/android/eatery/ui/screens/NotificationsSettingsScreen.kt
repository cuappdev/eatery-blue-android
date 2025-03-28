package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GraySix

@Composable
fun NotificationsSettingsScreen() {

    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Notifications",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )

        Text(
            text = "Manage item and promotional notifications",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SwitchOption(
            title = "All Notifications",
            description = "",
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )

        // TODO: Conditional visibility based on whether all notifications are enabled

        Spacer(modifier = Modifier.height(12.dp))

        SwitchOption(
            title = "Favorite item being served",
            description = "Get notified when favorite items are served",
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )

        SwitchOption(
            title = "Favorite eatery opening",
            description = "Get notified when your favorite eatery opens",
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )

        SwitchOption(
            title = "Favorite eatery closing",
            description = "Get notified when your favorite eatery closes",
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )
    }
}