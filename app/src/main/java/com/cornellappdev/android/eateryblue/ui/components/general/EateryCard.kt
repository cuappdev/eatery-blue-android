package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.theme.*
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
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
    val xMinutesUntilClosing = eatery.calculateTimeUntilClosing()?.collectAsStateWithLifecycle("")

    val interactionSource = MutableInteractionSource()
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp),
        onClick = {
            selectEatery(eatery)
        },
        backgroundColor = Color.White,
        modifier = modifier
    ) {
        Column {
            Box {
                GlideImage(
                    imageModel = { eatery.imageUrl ?: "" },
                    modifier = Modifier
                        .height(130.dp)
                        .fillMaxWidth(),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                    ),
                    component = rememberImageComponent {
                        +ShimmerPlugin(
                            baseColor = Color.White,
                            highlightColor = GrayZero,
                            durationMillis = 350,
                            dropOff = 0.65f,
                            tilt = 20f
                        )
                    },
                    failure = {
                        androidx.compose.foundation.Image(
                            modifier = Modifier
                                .height(130.dp)
                                .fillMaxWidth(),
                            painter = painterResource(R.drawable.blank_eatery),
                            contentDescription = "Eatery Image",
                            contentScale = ContentScale.Crop,
                        )
                    }
                )
                if (eatery.isClosed()) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(color = Color.White.copy(alpha = 0.53f))
                    )
                }
                if (!xMinutesUntilClosing?.value.isNullOrEmpty()) {
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
                                contentDescription = "Closing in 10 min",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = "Closing in ${xMinutesUntilClosing!!.value} min",
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
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DirectionsWalk,
                contentDescription = null,
                tint = GrayFive,
                modifier = Modifier.padding(end = 4.dp, top = 1.dp)
            )
            Text(
                text = "${eatery.getWalkTimes()} min walk",
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
