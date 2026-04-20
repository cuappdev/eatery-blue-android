package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors

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
            tint = currentColors.backgroundDefault92
        )
        Text(
            text = stringResource(R.string.no_eatery_found),
            style = EateryBlueTypography.h5,
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(100.dp),
            colors = buttonColors(
                containerColor = currentColors.accentPrimary
            ),
            onClick = {
                resetFilters()
            }
        ) {
            Text(
                text = stringResource(R.string.reset_filters),
                color =
                currentColors.backgroundDefault
            )
        }
    }
}
