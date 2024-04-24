package com.cornellappdev.android.eatery.ui.components.comparemenus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlue

@Composable
fun CompareMenusFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(16.dp),
        backgroundColor = EateryBlue
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_compare_menus), "Floating action button.", tint= Color.White)
    }
}