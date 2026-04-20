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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.repositories.CoilRepository
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.colorInterp
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.EateryPreview

enum class EateryCardStyle {
    DEFAULT, COMPACT, GRID_VIEW
}

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
    val xMinutesUntilClosing =
        eatery.calculateTimeUntilClosing()?.collectAsStateWithLifecycle()?.value

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

    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = currentColors.accentPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.clickable { selectEatery(eatery) }
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
                                    .background(
                                        colorInterp(
                                            progress,
                                            currentColors.backgroundSecondary,
                                            currentColors.backgroundDefault10
                                        )
                                    ),
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
                        colors = CardDefaults.cardColors(
                            containerColor = currentColors.accentPrimary,
                            contentColor = Color(0xFFFFA500)
                        )
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
                        color = currentColors.textPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 30.dp)
                    )
                    if (style != EateryCardStyle.GRID_VIEW) {
                        FavoriteButton(isFavorite, onFavoriteClick)
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
                indication = ripple(radius = 20.dp),
                onClick = onClick
            ),
        shape = CircleShape,
        color = currentColors.backgroundDefault
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
            tint = if (isFavorite) currentColors.accentPressed else currentColors.textSecondary,
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
                color = currentColors.textSecondary,
                style = EateryBlueTypography.subtitle2
            )
        }
    }
}

@Composable
fun EateryCardSecondaryHeader(eatery: Eatery, style: EateryCardStyle = EateryCardStyle.DEFAULT) {
    if (style != EateryCardStyle.COMPACT) {
        val walkText = eatery.getWalkTimeInMinutes()?.let {
            "${if (it > 0) it else "< 1"} min walk"
        }
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            walkText?.takeIf { style == EateryCardStyle.DEFAULT }?.let {
                Icon(
                    painter = painterResource(id = R.drawable.ic_walk_small),
                    contentDescription = null,
                    tint = currentColors.textSecondary,
                    modifier = Modifier.padding(end = 4.dp, top = 1.dp)
                )
                Text(
                    text = walkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = currentColors.textSecondary,
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
                color = if (openUntil == null) currentColors.error
                else if (eatery.isClosingSoon()) currentColors.accentPressed
                else currentColors.success
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
                color = currentColors.textSecondary,
                style = EateryBlueTypography.subtitle2
            )
            Text(
                text = "Carved Roast Beef",
                color = currentColors.textPrimary,
                fontStyle = FontStyle.Italic,
                style = EateryBlueTypography.subtitle2
            )
        }
    } else {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "Recommended for You: ",
                color = currentColors.textSecondary,
                style = EateryBlueTypography.subtitle2
            )
            Text(
                text = "Carved Roast Beef",
                color = currentColors.textPrimary,
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
        color = currentColors.textSecondary,
        style = EateryBlueTypography.subtitle2,
        modifier = Modifier.padding(horizontal = 5.dp)
    )
}

@Composable
fun EateryMenuSummary(eatery: Eatery) {
    if (eatery.acceptsMealSwipes()) {
        DotSeparator()
        Text(
            text = "Meal swipes allowed",
            maxLines = 1,
            color = currentColors.textPrimary,
            style = EateryBlueTypography.subtitle2
        )
    } else if (!eatery.acceptsBRB() &&
        (eatery.acceptsCash() || eatery.acceptsCard())
    ) {
        DotSeparator()
        Text(
            text = "Cash or credit only",
            maxLines = 1,
            color = currentColors.success,
            style = EateryBlueTypography.subtitle2
        )
    } else if (!eatery.menuSummary.isNullOrEmpty()) {
        DotSeparator()
        Text(
            text = eatery.menuSummary,
            maxLines = 1,
            color = currentColors.textSecondary,
            style = EateryBlueTypography.subtitle2
        )
    }
}

@Preview
@Composable
private fun EateryCardPreview() = EateryPreview {
    EateryCard(
        eatery = Eatery(
            id = 1,
            name = "Test Eatery",
            location = "Test Location",
            menuSummary = "Test Menu Summary"
        ),
        isFavorite = true,
        onFavoriteClick = {},
        style = EateryCardStyle.DEFAULT
    )
}