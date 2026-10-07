package com.cornellappdev.android.eatery.ui.components.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * One entry in the notification hub. [title] and [body] arrive pre-composed from the backend
 * and are shown as-is.
 */
@Composable
fun NotificationRow(
    title: String,
    body: String,
    isRead: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (isRead) painterResource(id = R.drawable.ic_notif_star)
            else painterResource(id = R.drawable.ic_new_notif_star),
            contentDescription = stringResource(R.string.a11y_notification_star_icon),
            tint = Color.Unspecified
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
                color = currentColors.textPrimary
            )
            Text(
                text = body,
                style = EateryBlueTypography.caption.copy(lineHeight = 16.sp),
                color = currentColors.textSecondary
            )
        }
    }
}

@DualModePreview
@Composable
private fun NotificationRowPreview() = EateryPreview {
    NotificationRow(
        title = "Chicken Nuggets",
        body = "At Becker House, Keeton House, Morrison Dining",
        isRead = true
    )
}
