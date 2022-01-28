package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.runtime.Composable
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.core.Text

@Composable
fun CategorySection(name: String, eateries: List<Eatery>) {
    Text(text = name)
}