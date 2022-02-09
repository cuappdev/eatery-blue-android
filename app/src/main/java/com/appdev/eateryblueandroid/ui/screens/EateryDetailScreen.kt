package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.Event
import com.appdev.eateryblueandroid.models.MenuCategory
import com.appdev.eateryblueandroid.models.MenuItem
import com.appdev.eateryblueandroid.ui.components.core.Image
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import java.time.LocalDateTime
import java.util.*

@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel,
    hideEatery: () -> Unit
) {
    val state = eateryDetailViewModel.state.collectAsState()
    state.value.let {
        when (it) {
            is EateryDetailViewModel.State.Empty ->
                Text("Error")
            is EateryDetailViewModel.State.Data ->
                Column(
                    modifier = Modifier.padding(0.dp)
                ) {
                    Box() {
                        Image(
                            url = it.data.imageUrl ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                        Button(
                            onClick = { hideEatery() },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .size(40.dp),
                            contentPadding = PaddingValues(6.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.white),
                                contentColor = colorResource(id = R.color.black)
                            )
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                        Button(
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(40.dp),
                            contentPadding = PaddingValues(6.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.white),
                                contentColor = colorResource(id = R.color.yellow)
                            )
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Favorite",
                            )
                        }
                        PaymentsWidget(
                            it.data,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .height(40.dp)
                        )
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = 18.dp, y = 18.dp)
                                .size(40.dp),
                            contentPadding = PaddingValues(6.dp),
                            elevation = null,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                disabledBackgroundColor = colorResource(id = R.color.white),
                                disabledContentColor = colorResource(id = R.color.gray05)
                            ),
                            enabled = false
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_place),
                                contentDescription = "Place",
                            )
                        }
                    }

                    Text(
                        text = it.data.name ?: "Loading...",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        textStyle = TextStyle.HEADER_H2,
                    )
                    Text(
                        text = "${it.data.location} · ${it.data.menuSummary}",
                        modifier = Modifier.padding(start = 16.dp),
                        textStyle = TextStyle.BODY_MEDIUM,
                        color = colorResource(id = R.color.gray05)
                    )
                    Row(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { /*TODO*/ },
                            shape = RoundedCornerShape(100),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.eateryBlue),
                                contentColor = colorResource(id = R.color.white)
                            )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_android_phone),
                                contentDescription = "Phone - Order Online"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp),
                                text = "Order online",
                                textStyle = TextStyle.HEADER_H4,
                                color = colorResource(id = R.color.white)
                            )
                        }
                        Button(
                            onClick = { /*TODO*/ },
                            shape = RoundedCornerShape(100),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.gray00),
                                contentColor = colorResource(id = R.color.black)
                            )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_walk),
                                contentDescription = "Walk - Get Directions"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp),
                                text = "Get directions",
                                textStyle = TextStyle.HEADER_H4,
                                maxLines = 1
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(id = R.color.blue_light))
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Warning",
                                tint = colorResource(id = R.color.eateryBlue)
                            )
                            Text(
                                text = "Cash or credit only after 3:00 PM",
                                textStyle = TextStyle.BODY_NORMAL,
                                color = colorResource(id = R.color.eateryBlue),
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }
                }
        }
    }

    BackHandler {
        hideEatery()
    }
}

@Composable
fun PaymentsWidget(eatery: Eatery, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.white))
                .padding(6.dp),

            ) {
            if (eatery.paymentAcceptsMealSwipes == true) {
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_payment_swipes),
                    contentDescription = "Accepts Swipes",
                    tint = colorResource(id = R.color.gray05)
                )
            }
            if (eatery.paymentAcceptsBrbs == true) {
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_payment_brbs),
                    contentDescription = "Accepts BRBs",
                    tint = colorResource(id = R.color.red)
                )
            }
            if (eatery.paymentAcceptsCash == true) {
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_payment_cash),
                    contentDescription = "Accepts Cash",
                    tint = colorResource(id = R.color.green)
                )
            }
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    val testingEatery = EateryDetailViewModel()
    testingEatery.selectEatery(
        Eatery(
            id = 1,
            name = "104West!",
            imageUrl = "https://raw.githubusercontent.com/cuappdev/assets/master/eatery/eatery-images/104-West.jpg",
            menuSummary = "Halal food",
            campusArea = "West",
            events = listOf(
                Event(
                    description = "Brunch",
                    canonicalDate = Date(),
                    startTime = LocalDateTime.now(),
                    endTime = LocalDateTime.now(),
                    menu = listOf(
                        MenuCategory(
                            category = "Soup Station",
                            items = listOf(
                                MenuItem(
                                    healthy = false,
                                    name = "Lox Cream Cheese",
                                    basePrice = null,
                                    description = null,
                                    sections = null
                                ),
                                MenuItem(
                                    healthy = false,
                                    name = "Waffle Bar",
                                    basePrice = null,
                                    description = null,
                                    sections = null
                                )
                            )
                        )
                    )
                )
            )
        )
    )
    EateryDetailScreen(eateryDetailViewModel = testingEatery) {

    }
}