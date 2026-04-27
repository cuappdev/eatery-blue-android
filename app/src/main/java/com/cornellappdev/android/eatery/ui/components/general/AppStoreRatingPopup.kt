package com.cornellappdev.android.eatery.ui.components.general

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.home.EateryDetailLoadingScreen
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun AppStoreRatingPopup(
    navigateToSupport: () -> Unit,
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository()
) {
    val showPopup = appStorePopupRepository.popupShowing.collectAsStateWithLifecycle().value
    if (showPopup) {
        Dialog(appStorePopupRepository::dismissPopup) {
            AppStoreRatingDialog(navigateToSupport, appStorePopupRepository::dismissPopup)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppStoreRatingDialog(navigateToSupport: () -> Unit, onDismiss: () -> Unit) {
    var rating by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val packageName = context.packageName

    AnimatedContent(targetState = rating) { currentRating ->
        when (currentRating) {
            0 -> RatingPrompt(rating, onChangeRating = { rating = it }, onDismiss = onDismiss)

            5 -> ActionPrompt(
                stringResource(R.string.app_store_rating_positive_message),
                stringResource(R.string.app_store_rating_positive_button),
                onButtonPress = {
                    try {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "market://details?id=$packageName".toUri()
                            )
                        )
                    } catch (_: ActivityNotFoundException) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/details?id=$packageName".toUri()
                            )
                        )
                    } finally {
                        onDismiss()
                    }
                },
                onDismiss = onDismiss,
            )

            else -> ActionPrompt(
                stringResource(R.string.app_store_rating_negative_message),
                stringResource(R.string.app_store_rating_negative_button),
                onButtonPress = { navigateToSupport(); onDismiss() },
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun ActionPrompt(
    actionText: String,
    buttonText: String,
    onButtonPress: () -> Unit,
    onDismiss: () -> Unit
) {
    AppStoreRatingCardBorder(onDismiss) {
        Text(
            actionText,
            style = EateryBlueTypography.h6,
            color = currentColors.textPrimary
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onButtonPress,
            colors = ButtonDefaults.buttonColors(containerColor = currentColors.accentPrimary),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text(
                buttonText,
                style = EateryBlueTypography.button,
                color = currentColors.backgroundDefault
            )
        }
    }
}

@DualModePreview
@Composable
private fun ActionPromptPreview() {
    ActionPrompt(actionText = "Awesome! We'd love to hear more in a review.",
        buttonText = "Open PlayStore", onButtonPress = {}, onDismiss = {})
}

@Composable
private fun RatingPrompt(rating: Int, onChangeRating: (Int) -> Unit, onDismiss: () -> Unit) {
    AppStoreRatingCardBorder(onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                stringResource(R.string.app_store_rating_question),
                style = EateryBlueTypography.h4,
                color = currentColors.textPrimary
            )
            RatingBar(rating, onChangeRating)
        }
    }
}

@Composable
private fun RatingBar(rating: Int, onChangeRating: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        (1..5).forEach {
            val selected = it <= rating
            Icon(
                if (selected) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                modifier = Modifier
                    .selectable(
                        selected = it <= rating,
                        onClick = { onChangeRating(it) }
                    )
                    .requiredSize(32.dp)
            )
        }
    }
}

@Composable
private fun AppStoreRatingCardBorder(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .shadow(3.dp, shape = RoundedCornerShape(20.dp))
            .background(currentColors.backgroundDefault)
            .clip(RoundedCornerShape(20.dp))
            .padding(bottom = 24.dp, start = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(end = 16.dp, top = 16.dp)
                    .size(20.dp)
                    .background(color = currentColors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = Icons.Default.Close.name,
                    tint = currentColors.textPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}


@Composable
@DualModePreview
private fun AppStoreRatingCardPreview() {
    EateryDetailLoadingScreen(shimmer = rememberShimmer(ShimmerBounds.View))
    Dialog(onDismissRequest = {}) { AppStoreRatingDialog(navigateToSupport = {}, onDismiss = {}) }
}
