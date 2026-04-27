package com.cornellappdev.android.eatery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.ThemeViewModel

val LocalColorMode = staticCompositionLocalOf { ColorTheme.lightMode }

@Composable
fun AppColorTheme(
    colorMode: ColorMode,
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(LocalColorMode provides colorMode) {
        content()
    }
}

val currentColors: ColorMode
    @Composable get() = LocalColorMode.current

@Composable
fun rememberResolvedDarkMode(themeViewModel: ThemeViewModel = hiltViewModel()): Boolean {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    return isDarkMode ?: isSystemInDarkTheme()
}

