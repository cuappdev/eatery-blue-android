package com.cornellappdev.android.eatery.ui.components.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors

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
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (isRead) painterResource(id = R.drawable.ic_notif_star)
            else painterResource(id = R.drawable.ic_new_notif_star),
            contentDescription = stringResource(R.string.a11y_notification_star_icon),
            tint = Color.Unspecified,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = EateryBlueTypography.h5,
                color = currentColors.textPrimary
            )
            Text(
                text = body,
                style = EateryBlueTypography.caption,
                color = currentColors.textSecondary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = currentColors.textPrimary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
