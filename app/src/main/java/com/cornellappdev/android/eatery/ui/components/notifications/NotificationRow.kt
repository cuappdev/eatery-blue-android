package com.cornellappdev.android.eatery.ui.components.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/**
 * One entry in the notification hub, showing the push the user was sent.
 */
@Composable
fun NotificationRow(
    body: String,
    isRead: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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
        Text(
            text = remember(body) { parseBoldMarkdown(body) },
            style = EateryBlueTypography.body2.copy(lineHeight = 18.sp),
            color = currentColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Bolds each `**…**` span in [text] and drops the markers. An unmatched `**` is left as-is.
 */
private fun parseBoldMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (true) {
        val start = text.indexOf("**", i)
        val end = if (start == -1) -1 else text.indexOf("**", start + 2)
        if (end == -1) {
            append(text.substring(i))
            break
        }
        append(text.substring(i, start))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(text.substring(start + 2, end))
        }
        i = end + 2
    }
}

@DualModePreview
@Composable
private fun NotificationRowPreview() = EateryPreview {
    Column {
        NotificationRow(
            body = "**Chicken Nuggets** is being served at Becker House at 5:35 PM!",
            isRead = false
        )
        NotificationRow(
            body = "**Pad Thai** is being served at Morrison Dining and 3 others at 10:30 AM!",
            isRead = true
        )
    }
}
