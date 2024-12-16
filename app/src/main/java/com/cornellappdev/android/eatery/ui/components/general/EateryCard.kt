package com.cornellappdev.android.eatery.ui.components.general

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.CoilRepository
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayOne
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Orange
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.ui.theme.Yellow
import com.cornellappdev.android.eatery.ui.theme.colorInterp
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse

enum class EateryCardStyle {
    DEFAULT, COMPACT, GRID_VIEW
}

@OptIn(
    ExperimentalMaterialApi::class,
)
@Composable
fun EateryCard(
    eatery: Eatery,
    isFavorite: Boolean,
    onFavoriteClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth(),
    style: EateryCardStyle = EateryCardStyle.DEFAULT,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    val xMinutesUntilClosing = eatery.calculateTimeUntilClosing()?.collectAsState()?.value

    val interactionSource = remember { MutableInteractionSource() }

    val bitmapState = eatery.imageUrl?.let { CoilRepository.getUrlState(it, LocalContext.current) }

    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = .5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
    )
    val closedAlpha by animateFloatAsState(
        targetValue = if (eatery.isClosed()) .53f else 1f,
        label = "Closed Fade",
        animationSpec = tween(250)
    )

    val imageHeight = when (style) {
        EateryCardStyle.GRID_VIEW -> 100.dp
        else -> 130.dp
    }

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
                                    .height(imageHeight)
                                    .fillMaxWidth(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )

                        is EateryApiResponse.Pending ->
                            Image(
                                bitmap = ImageBitmap(width = 1, height = 1),
                                modifier = Modifier
                                    .height(imageHeight)
                                    .fillMaxWidth()
                                    .background(colorInterp(progress, GrayOne, GrayThree)),
                                contentDescription = "",
                                contentScale = ContentScale.Crop
                            )

                        else ->
                            Image(
                                modifier = Modifier
                                    .height(imageHeight)
                                    .fillMaxWidth(),
                                painter = painterResource(R.drawable.blank_eatery),
                                contentDescription = "Eatery Image",
                                contentScale = ContentScale.Crop,
                            )
                    }
                }
                //TODO uncomment once backend finishes AI feature
//                if(!isGridView){
//                    DietaryWidgets(
//                        eatery,
//                        modifier = Modifier
//                            .align(Alignment.BottomEnd)
//                            .padding(16.dp)
//                            .height(40.dp)
//                    ) {
//                    }
//                }
                if (style == EateryCardStyle.GRID_VIEW) {
                    GridViewFavoriteWidget(
                        isFavorite = isFavorite,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .height(40.dp)
                    ) {
                        onFavoriteClick(!isFavorite)
                    }
                } else if (xMinutesUntilClosing != null && xMinutesUntilClosing <= 60) {
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
                    if (style != EateryCardStyle.GRID_VIEW) {
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
                }
                EateryCardPrimaryHeader(
                    eatery = eatery,
                    style = style
                )
                EateryCardSecondaryHeader(
                    eatery = eatery,
                    style = style
                )
                //TODO comment until backend is done with entree recommendation
//                EateryCardTertiaryHeader(
//                    eatery = eatery,
//                    style = style
//                )
            }
        }
    }
}

@Composable
fun GridViewFavoriteWidget(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .size(40.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(radius = 20.dp),
                onClick = onClick
            ),
        shape = CircleShape,
        color = Color.White
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
            tint = if (isFavorite) Yellow else GrayFive,
            modifier = Modifier
                .padding(8.dp),
            contentDescription = null
        )
    }
}


@Composable
fun EateryCardPrimaryHeader(eatery: Eatery, style: EateryCardStyle = EateryCardStyle.DEFAULT) {
    if (style == EateryCardStyle.DEFAULT) {
        Row {
            Text(
                text = eatery.location ?: "Unknown location",
                color = GrayFive,
                style = EateryBlueTypography.subtitle2
            )
        }
    }
}

@Composable
fun EateryCardSecondaryHeader(eatery: Eatery, style: EateryCardStyle = EateryCardStyle.DEFAULT) {
    if (style != EateryCardStyle.COMPACT) {
        val walkText =
            "${if (eatery.getWalkTimes()!! > 0) eatery.getWalkTimes() else "< 1"} min walk"
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (style == EateryCardStyle.DEFAULT) {
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
                DotSeparator()
            }
            val openUntil = eatery.getOpenUntil()
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text =
                if (openUntil == null) "Closed"
                else if (eatery.isClosingSoon()) "Closing at $openUntil"
                else ("Open until $openUntil"),
                style = EateryBlueTypography.subtitle2,
                color = if (openUntil == null) Red
                else if (eatery.isClosingSoon()) Yellow
                else Green
            )

        }
    }
}

//TODO, integrate backend for the entree recommendation once backend is done for that
@Composable
fun EateryCardTertiaryHeader(eatery: Eatery, style: EateryCardStyle = EateryCardStyle.DEFAULT) {
    //TODO uncomment when backend finishes AI feature
    if (style == EateryCardStyle.DEFAULT) {
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended for You: ",
                color = GrayFive,
                style = EateryBlueTypography.subtitle2
            )
            Text(
                text = "Carved Roast Beef",
                color = EateryBlue,
                fontStyle = FontStyle.Italic,
                style = EateryBlueTypography.subtitle2
            )
        }
    } else {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "Recommended for You: ",
                color = GrayFive,
                style = EateryBlueTypography.subtitle2
            )
            Text(
                text = "Carved Roast Beef",
                color = EateryBlue,
                fontStyle = FontStyle.Italic,
                style = EateryBlueTypography.subtitle2
            )
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
