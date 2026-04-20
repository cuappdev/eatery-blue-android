package com.cornellappdev.android.eatery.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalColorMode = staticCompositionLocalOf { ColorTheme.lightMode }

@Composable
fun AppColorTheme(
    colorMode: ColorMode ,
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(LocalColorMode provides colorMode) {
        content()
    }
}

val currentColors: ColorMode
    @Composable get() = LocalColorMode.current

