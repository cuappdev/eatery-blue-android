package com.appdev.eateryblueandroid.ui.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.ui.components.core.Image
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.util.saveRecentSearch

import kotlin.coroutines.coroutineContext

@Composable
fun EateryCard(
    eatery: Eatery,
    isCompact: Boolean = false,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    val interactionSource = MutableInteractionSource()
    Surface(
        elevation = 20.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .clickable {

                selectEatery(eatery) }
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(color = colorResource(id = R.color.white))
        ) {
            Box() {
                Image(
                    url = eatery.imageUrl ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.7f)
                )
                if (isClosed(eatery)) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.7f)
                            .background(color = colorResource(id = R.color.white_transparent))
                    )
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
                        textStyle = TextStyle.HEADER_H4,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                    Icon(
                        painter = painterResource(id = if (eatery.isFavorite()) R.drawable.ic_star else R.drawable.ic_star_outline),
                        tint = colorResource(id = if (eatery.isFavorite()) R.color.yellow else R.color.gray05),
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(radius = 9.dp),
                                onClick = {
                                    eatery.toggleFavorite()
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
                painter = painterResource(id = R.drawable.ic_watch),
                contentDescription = null,
                tint = colorResource(id = R.color.gray05),
                modifier = Modifier.padding(end = 4.dp, top = 1.dp)
            )
            Text(
                text = "10 - 12 min",
                color = colorResource(id = R.color.gray05),
                textStyle = TextStyle.BODY_MEDIUM
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
                color = colorResource(id = R.color.gray05),
                textStyle = TextStyle.BODY_MEDIUM
            )
            DotSeparator()
            EateryMenuSummary(eatery = eatery)
        }
    }
}

@Composable
fun EateryCardSecondaryHeader(eatery: Eatery, isCompact: Boolean) {
    if (!isCompact) {
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_watch),
                contentDescription = null,
                tint = colorResource(id = R.color.gray05),
                modifier = Modifier.padding(end = 4.dp, top = 1.dp)
            )
            Text(
                text = "${getWalkTimes(eatery)} min walk",
                color = colorResource(id = R.color.gray05),
                textStyle = TextStyle.BODY_MEDIUM
            )
            DotSeparator()
            Text(
                text = getWaitTimes(eatery) ?: "3-5 minutes",
                color = colorResource(id = R.color.gray05),
                textStyle = TextStyle.BODY_MEDIUM
            )
        }
    }
}

@Composable
fun DotSeparator() {
    Text(
        text = "??",
        color = colorResource(id = R.color.gray05),
        textStyle = TextStyle.BODY_MEDIUM,
        modifier = Modifier.padding(horizontal = 5.dp)
    )
}

@Composable
fun EateryMenuSummary(eatery: Eatery) {
    if (eatery.paymentAcceptsMealSwipes == true) {
        Text(
            text = "Meal swipes allowed",
            maxLines = 1,
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.BODY_MEDIUM
        )
    } else if (eatery.paymentAcceptsMealSwipes == false &&
        eatery.paymentAcceptsBrbs == false &&
        eatery.paymentAcceptsCash == true
    ) {
        Text(
            text = "Cash or credit only",
            maxLines = 1,
            color = colorResource(id = R.color.green),
            textStyle = TextStyle.BODY_MEDIUM
        )
    } else {
        Text(
            text = eatery.menuSummary ?: "",
            maxLines = 1,
            color = colorResource(id = R.color.gray05),
            textStyle = TextStyle.BODY_MEDIUM
        )
    }
}