package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.HubNotification
import com.cornellappdev.android.eatery.ui.components.general.LargeTitleHeader
import com.cornellappdev.android.eatery.ui.components.notifications.NOTIFICATIONS_STATE_HEIGHT_FRACTION
import com.cornellappdev.android.eatery.ui.components.notifications.NotificationRow
import com.cornellappdev.android.eatery.ui.components.notifications.NotificationsErrorState
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
    val isRefreshing by notificationsHomeViewModel.isRefreshing.collectAsStateWithLifecycle()

    NotificationsHomeScreenContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onBackClick = onBackClick,
        onRefresh = notificationsHomeViewModel::pullToRefresh,
        onTryAgain = notificationsHomeViewModel::refresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsHomeScreenContent(
    uiState: NotificationsHomeViewState,
    isRefreshing: Boolean,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onTryAgain: () -> Unit,
) {
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
                title = stringResource(R.string.notifications_home_error),
                onTryAgain = onTryAgain
            )

            is NotificationsHomeViewState.Loaded -> PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                // The pull gesture only reaches scrollable content, so the empty state is
                // wrapped in a LazyColumn as well.
                if (state.notifications.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            NotificationsEmptyState(
                                modifier = Modifier.fillParentMaxHeight(
                                    NOTIFICATIONS_STATE_HEIGHT_FRACTION
                                )
                            )
                        }
                    }
                } else {
                    Column {
                        FavoriteItemsHeading()
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.notifications,
                                key = { it.id }
                            ) { notification ->
                                NotificationRow(
                                    title = notification.title,
                                    body = notification.body,
                                    isRead = notification.isRead,
                                )
                            }
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
private fun NotificationsEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
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
private fun PreviewNotificationsHomeScreen(uiState: NotificationsHomeViewState) {
    NotificationsHomeScreenContent(
        uiState = uiState,
        isRefreshing = false,
        onBackClick = {},
        onRefresh = {},
        onTryAgain = {},
    )
}

@DualModePreview
@Composable
private fun NotificationsHomeScreenPreview() = EateryPreview {
    PreviewNotificationsHomeScreen(
        uiState = NotificationsHomeViewState.Loaded(
            listOf(
                HubNotification(
                    id = 1,
                    title = "Chicken Tenders",
                    body = "Now serving at Morrison Dining",
                    isRead = false
                ),
                HubNotification(
                    id = 2,
                    title = "Mac and Cheese",
                    body = "Now serving at Okenshields",
                    isRead = true
                ),
            )
        )
    )
}

@DualModePreview
@Composable
private fun NotificationsHomeEmptyPreview() = EateryPreview {
    PreviewNotificationsHomeScreen(uiState = NotificationsHomeViewState.Loaded(emptyList()))
}

@DualModePreview
@Composable
private fun NotificationsHomeLoadingPreview() = EateryPreview {
    PreviewNotificationsHomeScreen(uiState = NotificationsHomeViewState.Loading)
}

@DualModePreview
@Composable
private fun NotificationsHomeErrorPreview() = EateryPreview {
    PreviewNotificationsHomeScreen(uiState = NotificationsHomeViewState.Error)
}
