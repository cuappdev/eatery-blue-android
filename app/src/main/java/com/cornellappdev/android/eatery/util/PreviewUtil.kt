package com.cornellappdev.android.eatery.util

import androidx.compose.runtime.Composable
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme
import com.cornellappdev.android.eatery.ui.theme.ColorTheme

@Composable
fun EateryPreview(darkMode: Boolean = false, content: @Composable () -> Unit) {
    AppColorTheme(colorMode = if (darkMode) ColorTheme.darkMode else ColorTheme.lightMode)
    {
        content()
    }

}