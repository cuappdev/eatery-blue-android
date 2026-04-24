package com.cornellappdev.android.eatery.util

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme
import com.cornellappdev.android.eatery.ui.theme.ColorTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class DualModePreview

@Composable
fun EateryPreview(content: @Composable () -> Unit) {
    AppColorTheme(colorMode = if (isSystemInDarkTheme()) ColorTheme.darkMode else ColorTheme.lightMode) {
        content()
    }

}