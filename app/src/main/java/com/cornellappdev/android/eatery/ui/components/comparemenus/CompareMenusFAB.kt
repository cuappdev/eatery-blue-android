package com.cornellappdev.android.eatery.ui.components.comparemenus

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CompareMenusFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(16.dp),
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}