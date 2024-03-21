package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.repositories.CoilRepository
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayOne
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.Green
import com.cornellappdev.android.eateryblue.ui.theme.Orange
import com.cornellappdev.android.eateryblue.ui.theme.Yellow
import com.cornellappdev.android.eateryblue.ui.theme.colorInterp
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

//@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
//@Composable
//fun EateryRow(
//    modifier = Modifier
//            eatery : Eatery,
//    isFavorite: Boolean,
//    onFavoriteClick: (Boolean) -> Unit,
//    isCompact: Boolean = false,
//    selectEatery: (eatery: Eatery) -> Unit = {}
//) {
//    val xMinutesUntilClosing = eatery.calculateTimeUntilClosing()?.collectAsStateWithLifecycle("")
//
//    val interactionSource = MutableInteractionSource()
//    Card(
//        elevation = 10.dp,
//        shape = RoundedCornerShape(10.dp),
//        onClick = {
//            selectEatery(eatery)
//        },
//        backgroundColor = Color.White,
//        modifier = Modifier.width(400.dp)
//    ) {
//        Column {
//            Box {
//                GlideImage(
//                    imageModel = { eatery.imageUrl ?: "" },
//                    modifier = Modifier
//                        .height(120.dp)
//                        .fillMaxWidth(),
//                    imageOptions = ImageOptions(
//                        contentScale = ContentScale.Crop,
//                    ),
//                    component = rememberImageComponent {
//                        +ShimmerPlugin(
//                            baseColor = Color.White,
//                            highlightColor = GrayZero,
//                            durationMillis = 350,
//                            dropOff = 0.65f,
//                            tilt = 20f
//                        )
//                    },
//                    failure = {
//                        androidx.compose.foundation.Image(
//                            modifier = Modifier
//                                .height(120.dp)
//                                .fillMaxWidth(),
//                            painter = painterResource(R.drawable.blank_eatery),
//                            contentDescription = "Eatery Image",
//                            contentScale = ContentScale.Crop,
//                        )
//                    }
//                )
//                if (eatery.isClosed()) {
//                    Spacer(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(120.dp)
//                            .background(color = Color.White.copy(alpha = 0.53f))
//                    )
//                }
//                if (!xMinutesUntilClosing?.value.isNullOrEmpty()) {
//                    Card(
//                        modifier = Modifier
//                            .padding(top = 12.dp, end = 12.dp)
//                            .align(Alignment.TopEnd),
//                        shape = RoundedCornerShape(100.dp),
//                        contentColor = Orange,
//                        backgroundColor = Color.White
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(
//                                horizontal = 10.dp,
//                                vertical = 8.dp
//                            )
//                        ) {
//                            Icon(
//                                Icons.Outlined.Warning,
//                                contentDescription = "Closing in 10 min",
//                                modifier = Modifier.size(ButtonDefaults.IconSize)
//                            )
//                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                            Text(
//                                text = "Closing in ${xMinutesUntilClosing!!.value} min",
//                                style = EateryBlueTypography.button
//                            )
//                        }
//                    }
//                }
//            }
//            Column(
//                modifier = Modifier.padding(10.dp)
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        text = eatery.name ?: "",
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        style = EateryBlueTypography.h5,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(end = 30.dp)
//                    )
//                    Icon(
//                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
//                        tint = if (isFavorite) Yellow else GrayFive,
//                        modifier = Modifier
//                            .padding(top = 3.dp)
//                            .clickable(
//                                interactionSource = interactionSource,
//                                indication = rememberRipple(radius = 9.dp),
//                                onClick = {
//                                    onFavoriteClick(!isFavorite)
//                                }
//                            ),
//                        contentDescription = null
//                    )
//                }
//
//                EateryCardPrimaryHeader(eatery = eatery, isCompact = isCompact)
//                EateryCardSecondaryHeader(eatery = eatery, isCompact = isCompact)
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun EateryCard(
    eatery: Eatery,
    isFavorite: Boolean,
    onFavoriteClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth(),
    isCompact: Boolean = false,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    val xMinutesUntilClosing = eatery.calculateTimeUntilClosing()?.collectAsState()?.value

    val interactionSource = MutableInteractionSource()
    val bitmapState = eatery.imageUrl?.let { CoilRepository.getUrlState(it, LocalContext.current) }

    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = .5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val closedAlpha by animateFloatAsState(
        targetValue = if (eatery.isClosed()) .53f else 1f,
        label = "Closed Fade",
        animationSpec = tween(250)
    )

    Card(
        elevation = 3.dp,
        shape = RoundedCornerShape(10.dp),
        onClick = {
            selectEatery(eatery)
        },
        backgroundColor = Color.White,
        modifier = modifier
    ) {
        Column {
            Box {
                Crossfade(
                    targetState = bitmapState?.value,
                    label = "imageFade",
                    animationSpec = tween(250),
                    modifier = Modifier.alpha(closedAlpha)
                ) { apiResponse ->
                    when (apiResponse) {
                        is EateryApiResponse.Success ->
                            Image(
                                bitmap = apiResponse.data,
                                modifier = Modifier
                                    .height(130.dp)
                                    .fillMaxWidth(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )

                        is EateryApiResponse.Pending ->
                            Image(
                                bitmap = ImageBitmap(width = 1, height = 1),
                                modifier = Modifier
                                    .height(130.dp)
                                    .fillMaxWidth()
                                    .background(colorInterp(progress, GrayOne, GrayThree)),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )

                        else ->
                            Image(
                                modifier = Modifier
                                    .height(130.dp)
                                    .fillMaxWidth(),
                                painter = painterResource(R.drawable.blank_eatery),
                                contentDescription = "Eatery Image",
                                contentScale = ContentScale.Crop,
                            )
                    }
                }
                if (xMinutesUntilClosing != null && xMinutesUntilClosing <= 60) {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp, end = 12.dp)
                            .align(Alignment.TopEnd),
                        shape = RoundedCornerShape(100.dp),
                        contentColor = Orange,
                        backgroundColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Outlined.Warning,
                                contentDescription = "Closing soon",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = "Closing in $xMinutesUntilClosing min",
                                style = EateryBlueTypography.button
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = eatery.name ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = EateryBlueTypography.h5,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 30.dp)
                    )
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        tint = if (isFavorite) Yellow else GrayFive,
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(radius = 9.dp),
                                onClick = {
                                    onFavoriteClick(!isFavorite)
                                }
                            ),
                        contentDescription = null
                    )
                }
                EateryCardPrimaryHeader(eatery = eatery, isCompact = isCompact)
                EateryCardSecondaryHeader(eatery = eatery, isCompact = isCompact)
            }
        }
    }
}

@Composable
fun EateryCardPrimaryHeader(eatery: Eatery, isCompact: Boolean) {
    if (isCompact) {
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                tint = GrayFive,
                modifier = Modifier.padding(end = 4.dp, top = 1.dp)
            )

            Text(
                text = "${eatery.getWaitTimes() ?: "3-5"} min wait",
                color = GrayFive,
                style = EateryBlueTypography.subtitle2
            )

            DotSeparator()
            EateryMenuSummary(eatery = eatery)
        }
    } else {
        Row(
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = eatery.location ?: "Unknown location",
                color = GrayFive,
                style = EateryBlueTypography.subtitle2
            )
            EateryMenuSummary(eatery = eatery)
        }
    }
}

@Composable
fun EateryCardSecondaryHeader(eatery: Eatery, isCompact: Boolean) {
    if (!isCompact && eatery.getWalkTimes() != null) {
        val walkText =
            "${if (eatery.getWalkTimes()!! > 0) eatery.getWalkTimes() else "< 1"} min walk"
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_walk_small),
                contentDescription = null,
                tint = GrayFive,
                modifier = Modifier.padding(end = 4.dp, top = 1.dp)
            )
            Text(
                text = walkText,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = GrayFive,
                style = EateryBlueTypography.subtitle2
            )

            val waitTimes = eatery.getWaitTimes()
            if (!waitTimes.isNullOrEmpty()) {
                DotSeparator()
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = GrayFive,
                    modifier = Modifier.padding(end = 4.dp, top = 1.dp)
                )
                Text(
                    text = waitTimes,
                    color = GrayFive,
                    style = EateryBlueTypography.subtitle2
                )
            }

        }
    }
}

@Composable
fun DotSeparator() {
    Text(
        text = "·",
        color = GrayFive,
        style = EateryBlueTypography.subtitle2,
        modifier = Modifier.padding(horizontal = 5.dp)
    )
}

@Composable
fun EateryMenuSummary(eatery: Eatery) {
    if (eatery.paymentAcceptsMealSwipes == true) {
        DotSeparator()
        Text(
            text = "Meal swipes allowed",
            maxLines = 1,
            color = EateryBlue,
            style = EateryBlueTypography.subtitle2
        )
    } else if (eatery.paymentAcceptsBrbs == false &&
        eatery.paymentAcceptsCash == true
    ) {
        DotSeparator()
        Text(
            text = "Cash or credit only",
            maxLines = 1,
            color = Green,
            style = EateryBlueTypography.subtitle2
        )
    } else if (!eatery.menuSummary.isNullOrEmpty()) {
        DotSeparator()
        Text(
            text = eatery.menuSummary,
            maxLines = 1,
            color = GrayFive,
            style = EateryBlueTypography.subtitle2
        )
    }
}
