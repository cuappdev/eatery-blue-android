package com.cornellappdev.android.eatery.ui.components.comparemenus

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlue

@Composable
fun CompareMenusFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = modifier
            .padding(16.dp),
        backgroundColor = EateryBlue
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_compare_menus),
            "Compare Menus Floating Action Button",
            tint = Color.White
        )
    }
}
