package com.cornellappdev.android.eatery.ui.components.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.ErrorContent
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/** Share of the space below the header used to center the notification screens' full states. */
const val NOTIFICATIONS_STATE_HEIGHT_FRACTION = 0.7f

/** [ErrorContent] centered in the space below a notification screen's header. */
@Composable
fun NotificationsErrorState(title: String, onTryAgain: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight(NOTIFICATIONS_STATE_HEIGHT_FRACTION)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ErrorContent(title = title, onTryAgain = onTryAgain)
    }
}

@DualModePreview
@Composable
private fun NotificationsErrorStatePreview() = EateryPreview {
    NotificationsErrorState(
        title = stringResource(R.string.notifications_home_error),
        onTryAgain = {}
    )
}
