package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/**
 * The white circular back button that sits above a large page title.
 */
@Composable
fun CircularBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.16f),
                spotColor = Color.Black.copy(alpha = 0.16f)
            )
            .clip(CircleShape)
            .background(currentColors.backgroundDefault)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_left_chevron),
            contentDescription = stringResource(R.string.back),
            tint = currentColors.textPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Page header with a back button, a large brand-colored title and an optional [subtitle].
 * Handles the status bar inset itself, so callers should not add `statusBarsPadding`.
 */
@Composable
fun LargeTitleHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 7.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularBackButton(
            onClick = onBackClick,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Column {
            Text(
                text = title,
                color = currentColors.contentBrand,
                style = EateryBlueTypography.h3
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = currentColors.textSecondary,
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                )
            }
        }
    }
}

@DualModePreview
@Composable
private fun LargeTitleHeaderPreview() = EateryPreview {
    LargeTitleHeader(
        title = "Notifications",
        subtitle = "Manage item and promotional notifications",
        onBackClick = {}
    )
}
