package com.cornellappdev.android.eatery.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme

@Composable
fun EateryPreview(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    AppColorTheme {
        Column(
            modifier = modifier
                .background(Color.White)
        ) {
            content()
        }
    }

}