package com.cornellappdev.android.eatery.util

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme
import com.cornellappdev.android.eatery.ui.theme.ColorTheme

@Composable
fun EateryPreview(content : @Composable () -> Unit) {
    AppColorTheme(colorMode = ColorTheme.lightMode)
    {
        content()
    }

}