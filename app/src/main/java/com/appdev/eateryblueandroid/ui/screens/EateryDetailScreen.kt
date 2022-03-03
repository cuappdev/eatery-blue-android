package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Image
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.home.SearchBar
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.util.getMutableFavoriteStateOf
import com.appdev.eateryblueandroid.util.toggleFavorite
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel,
    hideEatery: () -> Unit
) {
    val state = eateryDetailViewModel.state.collectAsState()
    val favState : MutableState<Boolean>
    state.value.let {
        when(it){
            is EateryDetailViewModel.State.Data ->
                favState = getMutableFavoriteStateOf(it.data)
            else -> {favState = mutableStateOf(false)}
        }
    }
    state.value.let {
        when (it) {
            is EateryDetailViewModel.State.Empty ->
                Text("Error")
            is EateryDetailViewModel.State.Data ->
                Column(
                    modifier = Modifier
                        .padding(0.dp)
                        .verticalScroll(rememberScrollState())
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
                            onClick = { toggleFavorite(it.data) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(40.dp),
                            contentPadding = PaddingValues(6.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.white),
                                contentColor = colorResource(id = if (favState.value) R.color.yellow else R.color.gray05)
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = if (favState.value) R.drawable.ic_star else R.drawable.ic_star_outline),
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
                    AlertsSection(eatery = it.data)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .border(
                                1.dp,
                                colorResource(id = R.color.gray00),
                                RoundedCornerShape(8.dp)
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .weight(1f, true)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_clock),
                                    contentDescription = "Hours Icon",
                                    tint = colorResource(id = R.color.gray05)
                                )
                                Text(
                                    text = "Hours",
                                    textStyle = TextStyle.LABEL_SEMIBOLD,
                                    color = colorResource(id = R.color.gray05),
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                            val openUntil = getOpenUntil(it.data)
                            Text(
                                text = openUntil ?: "Closed",
                                textStyle = TextStyle.BODY_SEMIBOLD,
                                color = colorResource(id = if (openUntil.isNullOrBlank()) R.color.red else R.color.green),
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(colorResource(id = R.color.gray00), CircleShape)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .weight(1f, true)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_watch),
                                    contentDescription = "Watch Icon",
                                    tint = colorResource(id = R.color.gray05)
                                )
                                Text(
                                    text = "Wait Time",
                                    textStyle = TextStyle.LABEL_SEMIBOLD,
                                    color = colorResource(id = R.color.gray05),
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                            Text(
                                text = getWaitTimes(it.data) ?: "3-5 minutes",
                                textStyle = TextStyle.BODY_SEMIBOLD,
                                color = colorResource(id = R.color.black),
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(
                                colorResource(
                                    id = R.color.gray00
                                )
                            )
                    )
                    getCurrentEvents(it.data)?.forEach { event ->
                        EateryMenuWidget(event)
                    }
                }
        }
    }

    BackHandler {
        hideEatery()
    }
}

@Composable
fun EateryMenuWidget(event: Event) {
    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = event.description ?: "Full Menu",
                textStyle = TextStyle.HEADER_H3,
            )
            if (event.startTime != null && event.endTime != null) {
                Text(
                    text = "${event.startTime.format(DateTimeFormatter.ofPattern("K:mm a"))} - ${
                        event.endTime.format(
                            DateTimeFormatter.ofPattern("K:mm a")
                        )
                    }",
                    textStyle = TextStyle.BODY_MEDIUM,
                    color = colorResource(id = R.color.gray05)
                )
            }
        }
        CircularBackgroundIcon(
            icon = painterResource(
                id = R.drawable.ic_baseline_keyboard_arrow_down_24
            ),
            onTap = { /* TODO: */ },
            clickable = true,
            iconHeight = 28.dp,
            iconWidth = 28.dp,
        )
    }
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        SearchBar()
    }
    Spacer(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(colorResource(id = R.color.gray00), CircleShape)
    )
    event.menu?.forEach { category ->
        Text(
            text = category.category ?: "Category",
            textStyle = TextStyle.HEADER_H4,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        category.items?.forEach { menuItem ->
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row {
                    Text(
                        text = menuItem.name ?: "Item Name",
                        textStyle = TextStyle.BODY_SEMIBOLD,
                        modifier = Modifier.weight(1f)
                    )
                    if (menuItem.basePrice != null) {
                        Text(
                            text = String.format("$%.2f", menuItem.basePrice),
                            textStyle = TextStyle.BODY_MEDIUM
                        )
                    }

                }
                if (!menuItem.description.isNullOrBlank()) {
                    Text(
                        text = menuItem.description,
                        textStyle = TextStyle.BODY_NORMAL,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colorResource(id = R.color.gray00), CircleShape)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    colorResource(
                        id = R.color.gray00
                    )
                )
        )
    }
}

@Composable
fun AlertsSection(eatery: Eatery) {
    eatery.alerts?.forEach {
        if (!it.description.isNullOrBlank()
            && it.startTime?.isBefore(LocalDateTime.now()) == true
            && it.endTime?.isAfter(LocalDateTime.now()) == true
        )
            Surface(
                modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
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
                        text = it.description,
                        textStyle = TextStyle.BODY_NORMAL,
                        color = colorResource(id = R.color.eateryBlue),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
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