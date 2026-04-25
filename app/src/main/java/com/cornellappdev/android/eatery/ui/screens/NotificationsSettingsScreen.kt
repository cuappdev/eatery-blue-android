package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.SwitchOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun NotificationsSettingsScreen() {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.notifications_title),
            color = currentColors.textPrimary,
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
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )

        // TODO: Conditional visibility based on whether all notifications are enabled

        Spacer(modifier = Modifier.height(12.dp))

        SwitchOption(
            title = stringResource(R.string.notifications_favorite_item_title),
            description = stringResource(R.string.notifications_favorite_item_description),
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )

        SwitchOption(
            title = stringResource(R.string.notifications_favorite_eatery_open_title),
            description = stringResource(R.string.notifications_favorite_eatery_open_description),
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )

        SwitchOption(
            title = stringResource(R.string.notifications_favorite_eatery_close_title),
            description = stringResource(R.string.notifications_favorite_eatery_close_description),
            initialValue = true,
            onCheckedChange = {
//                TODO()
            }
        )
    }
}

@DualModePreview
@Composable
private fun NotificationsSettingsScreenPreview() = EateryPreview {
    NotificationsSettingsScreen()
}
