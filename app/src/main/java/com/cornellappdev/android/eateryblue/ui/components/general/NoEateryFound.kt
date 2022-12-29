package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayTwo

@Composable
fun NoEateryFound(modifier: Modifier = Modifier, resetFilters: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Icon(
            painter = painterResource(R.drawable.ic_eaterylogo),
            contentDescription = null,
            modifier = Modifier
                .height(72.dp)
                .width(72.dp),
            tint = GrayTwo
        )
        Text(
            text = "No eatery found...",
            style = EateryBlueTypography.h5,
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(100.dp),
            colors = buttonColors(
                backgroundColor = EateryBlue
            ),
            onClick = {
                resetFilters()
            }
        ) {
            Text(
                text = "Reset filters",
                color =
                Color.White
            )
        }
    }
}
