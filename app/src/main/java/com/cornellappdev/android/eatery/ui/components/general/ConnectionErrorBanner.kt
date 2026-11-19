package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/** Shown over stale content when the latest data couldn't be fetched. */
@Composable
fun ConnectionErrorBanner(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textStyle = EateryBlueTypography.caption.copy(
        lineHeight = 1.4.em,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = currentColors.backgroundDefault,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi_slash),
                contentDescription = null,
                tint = currentColors.contentBrand
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.connection_error_title),
                    style = textStyle.copy(fontWeight = FontWeight.Bold),
                    color = currentColors.textPrimary
                )
                Text(
                    text = stringResource(R.string.connection_error_description),
                    style = textStyle.copy(fontWeight = FontWeight.Medium),
                    color = currentColors.textPrimary
                )
            }
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentColors.contentBrand,
                    contentColor = currentColors.oppTextPrimary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.connection_error_retry),
                    style = EateryBlueTypography.caption.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@DualModePreview
@Composable
private fun ConnectionErrorBannerPreview() = EateryPreview {
    ConnectionErrorBanner(onRetry = {}, modifier = Modifier.padding(6.dp))
}
