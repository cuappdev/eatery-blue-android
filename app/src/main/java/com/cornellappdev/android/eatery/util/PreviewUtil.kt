package com.cornellappdev.android.eatery.util

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme
import com.cornellappdev.android.eatery.ui.theme.ColorTheme
import com.cornellappdev.android.eatery.ui.theme.currentColors

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class DualModePreview

@Composable
fun EateryPreview(content: @Composable () -> Unit) {
    AppColorTheme(colorMode = if (isSystemInDarkTheme()) ColorTheme.darkMode else ColorTheme.lightMode) {
        Box(modifier = Modifier.background(currentColors.backgroundDefault)) {
            content()
        }
    }

}