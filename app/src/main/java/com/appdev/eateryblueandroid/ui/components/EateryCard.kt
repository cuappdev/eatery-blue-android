package com.appdev.eateryblueandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.eateryblueandroid.models.Eatery
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.glide.GlideImage
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.theme.sfProTextFontFamily
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color

@Composable
fun EateryCard(eatery: Eatery, isCompact: Boolean = false) {
    Surface(
        elevation = 20.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color = colorResource(id = R.color.white))
        ) {
            GlideImage(
                imageModel = eatery.imageUrl,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.7f),
                shimmerParams = ShimmerParams(
                    baseColor = colorResource(id = R.color.white),
                    highlightColor = colorResource(id =R.color.gray00),
                    durationMillis = 350,
                    dropOff = 0.65f,
                    tilt = 20f
                ),
                failure = {
                    Image(
                        painter = painterResource(R.drawable.blank_eatery),
                        contentDescription = "Eatery Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = eatery.name ?: "Unnamed Eatery",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(id = R.color.black),
                        fontFamily = sfProTextFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star_outline),
                        tint = colorResource(id = R.color.gray05),
                        modifier = Modifier.padding(top = 3.dp),
                        contentDescription = null
                    )
                }

                EateryCardPrimaryHeader(eatery = eatery, isCompact = isCompact)
                EateryCardSecondaryHeader(eatery = eatery, isCompact = isCompact )
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
                fontFamily = sfProTextFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
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
                fontFamily = sfProTextFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
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
                text = "3 min walk",
                color = colorResource(id = R.color.gray05),
                fontFamily = sfProTextFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            DotSeparator()
            Text(
                text = "5-7 min wait",
                color = colorResource(id = R.color.gray05),
                fontFamily = sfProTextFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DotSeparator() {
    Text(
        text = "·",
        color = colorResource(id = R.color.gray05),
        fontFamily = sfProTextFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 5.dp)
    )
}

@Composable
fun EateryMenuSummary(eatery: Eatery) {
    if (eatery.paymentAcceptsMealSwipes == true) {
        Text(
            text = "Meal swipes allowed",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(id = R.color.eateryBlue),
            fontFamily = sfProTextFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    } else if (eatery.paymentAcceptsMealSwipes == false &&
            eatery.paymentAcceptsBrbs == false &&
            eatery.paymentAcceptsCash == true) {
        Text(
            text = "Cash or credit only",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(id = R.color.green),
            fontFamily = sfProTextFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    } else {
        Text(
            text = eatery.menuSummary ?: "No menu summary",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(id = R.color.gray05),
            fontFamily = sfProTextFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}