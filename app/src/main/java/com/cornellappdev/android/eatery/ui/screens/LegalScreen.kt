package com.cornellappdev.android.eatery.ui.screens

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun LegalScreen() {
    val uriCurrent = LocalUriHandler.current
    val colors = currentColors
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.legal_title),
            color = colors.textPrimary,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = stringResource(R.string.legal_description),
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SettingsOption(
            title = stringResource(R.string.legal_terms_and_conditions),
            onClick = { uriCurrent.openUri("https://www.cornellappdev.com/privacy") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = colors.backgroundSecondary,
                )
            }
        )
        SettingsLineSeparator()
        SettingsOption(
            title = stringResource(R.string.legal_privacy_policy),
            onClick = { uriCurrent.openUri("https://www.cornellappdev.com/privacy") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = colors.backgroundSecondary,
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LegalScreenPreview() = EateryPreview {
    LegalScreen()
}

