package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/**
 * Full-screen network error with a [title] and a Try Again button.
 */
@Composable
fun ErrorContent(
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.home_error_title),
) {
    Column(
        modifier = modifier
            .width(293.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = stringResource(R.string.a11y_home_error_icon_desc),
            modifier = Modifier.size(72.dp),
            tint = currentColors.error
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = currentColors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_error_description),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = currentColors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onTryAgain,
            modifier = Modifier
                .width(109.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = currentColors.accentPrimary)
        ) {
            Text(
                text = stringResource(R.string.home_error_try_again),
                color = currentColors.textPrimary,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 1.25.em
            )
        }
    }
}

@DualModePreview
@Composable
private fun ErrorContentPreview() = EateryPreview {
    ErrorContent(onTryAgain = {})
}
