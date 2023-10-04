package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eateryblue.ui.components.settings.SettingsOption
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GraySix

@Composable
fun LegalScreen() {
    val uriCurrent = LocalUriHandler.current
    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Legal",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Find terms, conditions, and privacy policy",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SettingsOption(
            title = "Terms and Conditions",
            onClick = { uriCurrent.openUri("https://www.cornellappdev.com/privacy") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = EateryBlue,
                )
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
