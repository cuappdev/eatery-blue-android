package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.LargeTitleHeader
import com.cornellappdev.android.eatery.ui.components.notifications.NotificationRow
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.NotificationsHomeViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.NotificationsHomeViewState
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun NotificationsHomeScreen(
    onBackClick: () -> Unit,
    notificationsHomeViewModel: NotificationsHomeViewModel = hiltViewModel(),
) {
    val uiState by notificationsHomeViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
            .fillMaxSize()
    ) {
        LargeTitleHeader(
            title = stringResource(R.string.notifications_home_title),
            onBackClick = onBackClick
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is NotificationsHomeViewState.Loading -> {
                FavoriteItemsHeading()
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    NotificationsLoadingState()
                }
            }

            is NotificationsHomeViewState.Error -> NotificationsErrorState(
                onTryAgain = notificationsHomeViewModel::refresh
            )

            is NotificationsHomeViewState.Loaded -> {
                if (state.notifications.isEmpty()) {
                    NotificationsEmptyState()
                } else {
                    FavoriteItemsHeading()
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            items = state.notifications,
                            key = { it.id }
                        ) { notification ->
                            NotificationRow(
                                title = notification.title,
                                body = notification.body,
                                isRead = notification.isRead,
                                onClick = {
                                    notificationsHomeViewModel.markAsRead(notification.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemsHeading() {
    Text(
        text = stringResource(R.string.notifications_home_favorite_items),
        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        color = currentColors.textPrimary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
    )
}

/**
 * Shimmering placeholder rows, matching the loading treatment used on the home and favorites
 * screens. Only shown on a cold start, since cached notifications render immediately.
 */
@Composable
private fun NotificationsLoadingState() {
    val shimmer = rememberShimmer(ShimmerBounds.View)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            Surface(
                modifier = Modifier
                    .shimmer(shimmer)
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .height(72.dp),
                color = currentColors.backgroundDefault92
            ) {}
        }
    }
}

@Composable
private fun NotificationsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(252.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notifications_bell_empty),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.notifications_home_empty),
                style = EateryBlueTypography.h6,
                color = currentColors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.notifications_home_empty_description),
                style = EateryBlueTypography.caption.copy(lineHeight = 16.sp),
                color = currentColors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun NotificationsErrorState(onTryAgain: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
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
                text = stringResource(R.string.notifications_home_error),
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.25.em
                )
            }
        }
    }
}

@DualModePreview
@Composable
private fun NotificationsHomeScreenPreview() = EateryPreview {
    NotificationsHomeScreen(onBackClick = {})
}
