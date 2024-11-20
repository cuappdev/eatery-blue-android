package com.cornellappdev.android.eatery.ui.components.general

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.cornellappdev.android.eatery.ui.components.home.EateryDetailLoadingScreen
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun AppStoreRatingPopup(
    navigateToSupport: () -> Unit,
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository()
) {
    val showPopup = appStorePopupRepository.popupShowing.collectAsState().value
    if (showPopup) {
        Dialog(appStorePopupRepository::dismissPopup) {
            AppStoreRatingDialog(navigateToSupport, appStorePopupRepository::dismissPopup)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppStoreRatingDialog(navigateToSupport: () -> Unit, dismiss: () -> Unit) {
    var rating by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val packageName = context.packageName

    AnimatedContent(targetState = rating) { currentRating ->
        when (currentRating) {
            0 -> RatingPrompt(rating, onChangeRating = { rating = it })

            5 -> ActionPrompt("Awesome! We'd love to hear more on the PlayStore",
                "Open PlayStore",
                action = {
                    try {
                        startActivity(
                            context, Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$packageName")
                            ), null
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            context,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            ),
                            null
                        )
                    } finally {
                        dismiss()
                    }
                })

            else -> ActionPrompt(
                "Sorry to hear that.",
                "Visit support",
                action = { navigateToSupport(); dismiss() }
            )
        }
    }
}

@Composable
private fun ActionPrompt(actionText: String, buttonText: String, action: () -> Unit) {
    AppStoreRatingCardBorder {
        Text(actionText, style = EateryBlueTypography.subtitle2)
        Spacer(Modifier.height(12.dp))
        Button(
            action,
            colors = ButtonDefaults.buttonColors(backgroundColor = EateryBlue),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text(
                buttonText,
                style = EateryBlueTypography.button,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun ActionPromptPreview() {
    ActionPrompt(actionText = "Awesome, we'd love to hear about it on the PlayStore!",
        buttonText = "Visit the PlayStore", action = {})
}

@Composable
private fun RatingPrompt(rating: Int, onChangeRating: (Int) -> Unit) {
    AppStoreRatingCardBorder {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("How is your experience so far?", style = EateryBlueTypography.h4)
            RatingBar(rating, onChangeRating)
        }
    }
}

@Composable
private fun RatingBar(rating: Int, onChangeRating: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        (1..5).map {
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
private fun AppStoreRatingCardBorder(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .shadow(3.dp, shape = RoundedCornerShape(20.dp))
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        content = content,
    )
}


@Composable
@Preview
private fun AppStoreRatingCardPreview() {
    EateryDetailLoadingScreen(shimmer = rememberShimmer(ShimmerBounds.View))
    Dialog(onDismissRequest = {}) { AppStoreRatingDialog(navigateToSupport = {}, dismiss = {}) }
}