package com.cornellappdev.android.eateryblue.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.data.repositories.CoilRepository
import com.cornellappdev.android.eateryblue.ui.components.general.PaymentMethodsAvailable
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eateryblue.ui.components.home.EateryDetailLoadingScreen
import com.cornellappdev.android.eateryblue.ui.components.settings.Issue
import com.cornellappdev.android.eateryblue.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayOne
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.theme.Green
import com.cornellappdev.android.eateryblue.ui.theme.LightBlue
import com.cornellappdev.android.eateryblue.ui.theme.Red
import com.cornellappdev.android.eateryblue.ui.theme.Yellow
import com.cornellappdev.android.eateryblue.ui.theme.colorInterp
import com.cornellappdev.android.eateryblue.ui.viewmodels.EateryDetailViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel = hiltViewModel()
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val context = LocalContext.current
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetContent by remember { mutableStateOf(BottomSheetContent.PAYMENT_METHODS_AVAILABLE) }
    val paymentMethods = remember { mutableStateListOf<PaymentMethodsAvailable>() }
    val coroutineScope = rememberCoroutineScope()
    val issue by remember { mutableStateOf<Issue?>(null) }

    when (val eateryApiResponse = eateryDetailViewModel.eatery.value) {
        is EateryApiResponse.Pending -> {
            EateryDetailLoadingScreen(shimmer)
        }

        is EateryApiResponse.Error -> {
            Text(text = "ERROR")
        }

        is EateryApiResponse.Success -> {
            val eatery = eateryApiResponse.data
            val bitmapState =
                eatery.imageUrl?.let { CoilRepository.getUrlState(it, LocalContext.current) }
            val infiniteTransition = rememberInfiniteTransition()
            val progress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = .5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            ModalBottomSheetLayout(
                sheetState = modalBottomSheetState, sheetContent = {
                    when (sheetContent) {
                        BottomSheetContent.PAYMENT_METHODS_AVAILABLE -> {
                            PaymentMethodsAvailable(selectedPaymentMethods = paymentMethods) {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        BottomSheetContent.REPORT -> {
                            eatery.id?.let {
                                ReportBottomSheet(issue = issue,
                                    eateryid = it,
                                    sendReport = { issue, report, eateryid ->
                                        eateryDetailViewModel.sendReport(issue, report, eateryid)
                                    }) {
                                    coroutineScope.launch {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            }
                        }

                        // TODO: Hours bottom sheet doesn't seem to exist yet.

                        else -> {}
                    }
                }, sheetShape = RoundedCornerShape(
                    bottomStart = 0.dp, bottomEnd = 0.dp, topStart = 12.dp, topEnd = 12.dp
                ), sheetElevation = 8.dp
            ) {

                paymentMethods.apply {
                    if (eatery.paymentAcceptsCash == true) add(PaymentMethodsAvailable.CASH)
                    if (eatery.paymentAcceptsBrbs == true) add(PaymentMethodsAvailable.BRB)
                    if (eatery.paymentAcceptsMealSwipes == true) add(PaymentMethodsAvailable.SWIPES)
                }
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Box {
                        Box {
                            Crossfade(
                                targetState = bitmapState?.value,
                                label = "imageFade",
                                animationSpec = tween(250),
                                modifier = Modifier.alpha(if (eatery.isClosed()) .53f else 1f)
                            ) { apiResponse ->
                                when (apiResponse) {
                                    is EateryApiResponse.Success -> {
                                        Image(
                                            bitmap = apiResponse.data,
                                            modifier = Modifier
                                                .height(240.dp)
                                                .fillMaxWidth(),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    is EateryApiResponse.Pending -> {
                                        Image(
                                            bitmap = ImageBitmap(width = 1, height = 1),
                                            modifier = Modifier
                                                .height(240.dp)
                                                .fillMaxWidth()
                                                .background(
                                                    colorInterp(
                                                        progress,
                                                        GrayOne,
                                                        GrayThree
                                                    )
                                                ),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    else -> {
                                        Image(
                                            modifier = Modifier
                                                .height(240.dp)
                                                .fillMaxWidth(),
                                            painter = painterResource(R.drawable.blank_eatery),
                                            contentDescription = "Eatery Image",
                                            contentScale = ContentScale.Crop,
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { eateryDetailViewModel.toggleFavorite() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 40.dp, end = 16.dp)
                                .size(40.dp),
                            contentPadding = PaddingValues(6.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                            )
                        ) {
                            Icon(
                                imageVector = if (eateryDetailViewModel.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                tint = if (eateryDetailViewModel.isFavorite) Yellow else GrayFive,
                                contentDescription = null
                            )
                        }

                        PaymentsWidget(
                            eatery,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .height(40.dp)
                        ) {
                            sheetContent = BottomSheetContent.PAYMENT_METHODS_AVAILABLE
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
                        }
                    }

                    Column(modifier = Modifier.background(Color.White)) {
                        Text(
                            text = eatery.name ?: "Loading...",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            style = EateryBlueTypography.h3,
                        )
                        Text(
                            text = "${eatery.location} ${if (!eatery.menuSummary.isNullOrBlank()) "· ${eatery.menuSummary}" else ""}",
                            modifier = Modifier.padding(start = 16.dp),
                            style = EateryBlueTypography.subtitle2,
                            color = GrayFive
                        )
                        Row(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val getAppIntent =
                                        context.packageManager.getLaunchIntentForPackage("com.cbord.get")
                                    if (getAppIntent != null) {
                                        getAppIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                                        context.startActivity(getAppIntent)
                                    } else {
                                        val openPlayIntent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(
                                                "https://play.google.com/store/apps/details?id=com.cbord.get"
                                            )
                                            setPackage("com.android.vending")
                                        }
                                        context.startActivity(openPlayIntent)
                                    }
                                },
                                shape = RoundedCornerShape(100),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = EateryBlue, contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_android_phone),
                                    contentDescription = "Phone - Order Online"
                                )
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    text = "Order online",
                                    style = EateryBlueTypography.h5,
                                    color = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    val mapIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data =
                                            Uri.parse("google.navigation:q=${eatery.latitude},${eatery.longitude}&mode=w")
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent)
                                },
                                shape = RoundedCornerShape(100),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GrayZero, contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_walk),
                                    contentDescription = "Walk - Get Directions"
                                )
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    text = "Get directions",
                                    style = EateryBlueTypography.h5,
                                    maxLines = 1
                                )
                            }
                        }

                        AlertsSection(eatery = eatery)

                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .border(
                                    1.dp, GrayZero, RoundedCornerShape(8.dp)
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
                                    modifier = Modifier.clickable {
                                        sheetContent = BottomSheetContent.HOURS
                                        coroutineScope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Schedule,
                                        contentDescription = "Hours Icon",
                                        tint = GrayFive
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(
                                        text = "Hours", style = TextStyle(
                                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                        ), color = GrayFive
                                    )
                                }
                                val openUntil = eatery.getOpenUntil()
                                Text(
                                    modifier = Modifier.padding(top = 2.dp),
                                    text = openUntil ?: "Closed",
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                    ),
                                    color = if (openUntil.isNullOrBlank()) Red else Green,
                                )
                            }

                            Divider(
                                color = GrayZero,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .fillMaxHeight(0.5f)
                                    .width(1.dp)
                            )

//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                modifier = Modifier
//                                    .padding(vertical = 12.dp)
//                                    .weight(1f, true)
//                            ) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.clickable {
//                                        sheetContent = BottomSheetContent.WAIT_TIME
//                                        coroutineScope.launch {
//                                            modalBottomSheetState.show()
//                                        }
//                                    }
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.HourglassTop,
//                                        contentDescription = "Watch Icon",
//                                        tint = GrayFive
//                                    )
//                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
//                                    Text(
//                                        text = "Wait Time",
//                                        style = TextStyle(
//                                            fontWeight = FontWeight.SemiBold,
//                                            fontSize = 16.sp
//                                        ),
//                                        color = GrayFive
//                                    )
//                                }
//
//                                val waitTimes = eatery.getWaitTimes()
//                                Text(
//                                    modifier = Modifier.padding(top = 2.dp),
//                                    text = if (!waitTimes.isNullOrEmpty() && !eatery.isClosed()) {
//                                        "$waitTimes minutes"
//                                    } else {
//                                        "-"
//                                    },
//                                    style = TextStyle(
//                                        fontWeight = FontWeight.SemiBold,
//                                        fontSize = 16.sp
//                                    ),
//                                    color = Color.Black,
//                                )
//
//
//                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(GrayZero)
                        )

                        val nextEvent: Event? = eatery.getCurrentEvent()
                        if (nextEvent != null) {
                            EateryMenuWidget(event = nextEvent)
                        }


                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(GrayZero)
                        )

                        // Report an issue button
                        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                            Text(
                                text = "Make Eatery Better",
                                style = EateryBlueTypography.h5,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = "Help us make this info more accurate by letting us know what's wrong.",
                                style = EateryBlueTypography.body2,
                                modifier = Modifier.padding(bottom = 5.dp),
                                color = GrayFive
                            )

                            Spacer(Modifier.height(8.dp))
                            //reporting button
                            Button(
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .height(35.dp)
                                    .shadow(0.dp),
                                onClick = {
                                    sheetContent = BottomSheetContent.REPORT
                                    coroutineScope.launch {
                                        modalBottomSheetState.show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GrayZero,
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
                                    text = "Report an Issue",
                                    style = EateryBlueTypography.button,
                                    color = Color.Black
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(GrayZero)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentsWidget(eatery: Eatery, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable {
            onClick.invoke()
        }, shape = CircleShape, color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)
        ) {
            if (eatery.paymentAcceptsMealSwipes == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_swipes),
                    contentDescription = "Accepts Swipes",
                    tint = EateryBlue
                )
            }
            if (eatery.paymentAcceptsBrbs == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_brbs),
                    contentDescription = "Accepts BRBs",
                    tint = Red
                )
            }
            if (eatery.paymentAcceptsCash == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_cash),
                    contentDescription = "Accepts Cash",
                    tint = Green
                )
            }
        }
    }
}


@Composable
fun AlertsSection(eatery: Eatery) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)
    ) {

        eatery.alerts?.forEach {
            if (!it.description.isNullOrBlank() && it.startTime?.isBefore(LocalDateTime.now()) == true && it.endTime?.isAfter(
                    LocalDateTime.now()
                ) == true
            ) Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBlue)
                ) {
                    Icon(
                        Icons.Default.Info, contentDescription = "Warning", tint = EateryBlue
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = it.description,
                        style = EateryBlueTypography.body2,
                        color = EateryBlue,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EateryMenuWidget(event: Event) {
    /** Handles the number and calender at the top*/
    val zoneId: ZoneId? = ZoneId.of("America/New_York")
    val today = LocalDate.now(zoneId)
    val currentDay by remember { mutableStateOf(today) }
    Log.d("current day", currentDay.dayOfWeek.value.toString())
    Log.d("current day2", currentDay.dayOfMonth.toString())
    val dayWeek: Int = currentDay.dayOfWeek.value
    val dayNum: Int = currentDay.dayOfMonth
    val dayNames = mutableListOf<String>()
    val dayWeeks = mutableListOf<Int>()
    val days = mutableListOf<Int>()


    dayWeeks.add(dayWeek)
    days.add(dayNum)
    for (i in 1 until 7) {
        dayWeeks.add(currentDay.plusDays(i.toLong()).dayOfWeek.value)
        days.add(currentDay.plusDays(i.toLong()).dayOfMonth)
    }
    Log.d("list for cal", dayWeeks.toList().toString())
    dayWeeks.forEach {
        var dayName = ""
        when (it) {
            1 -> dayName = "Mon"
            2 -> dayName = "Tues"
            3 -> dayName = "Wed"
            4 -> dayName = "Thurs"
            5 -> dayName = "Fri"
            6 -> dayName = "Sat"
            7 -> dayName = "Sun"

        }
        dayNames.add(dayName)
    }

    rememberCoroutineScope()

    val weekDayIndex = 0
    var selectedDay by remember { mutableStateOf(weekDayIndex) }
    var openUpcoming by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = event.description ?: "Full Menu",
                style = EateryBlueTypography.h4,
            )
            if (event.startTime != null && event.endTime != null) {
                Text(
                    text = "${event.startTime.format(DateTimeFormatter.ofPattern("K:mm a"))} - ${
                        event.endTime.format(
                            DateTimeFormatter.ofPattern("K:mm a")
                        )
                    }", style = EateryBlueTypography.subtitle2, color = GrayFive
                )
            }

        }
        IconButton(
            onClick = {
                openUpcoming = true
            },
            modifier = Modifier
                .padding(all = 8.dp)
                .background(color = GrayZero, shape = CircleShape)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_calendar),
                contentDescription = "Expand menu",
                modifier = Modifier.size(26.dp)
            )
        }
    }

    /** If the calendar icon is pressed a dialog pops up allowing the user to select through
     * the upcoming meals at a specific dining hall */
    if (openUpcoming) {
        Dialog(
            // Allow dialog box to span the entire page and make it disappear when openUpcoming is false
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { openUpcoming = false }

        ) {
            // Get the dialog window and set it to the bottom using gravity
            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
            dialogWindowProvider.window.setGravity(Gravity.BOTTOM)
            var currSelectedDay by remember { mutableStateOf(selectedDay) }
            // Dialog Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = 15.dp, start = 15.dp, end = 15.dp
                    ),
                ) {
                    // Menus & X
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Menus",
                                style = EateryBlueTypography.h4,
                            )
                        }
                        IconButton(
                            onClick = {
                                openUpcoming = false
                            },
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .background(color = GrayZero, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Upcoming",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Upcoming Day selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0..6) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayNames[i].uppercase(),
                                    color = GrayFive,
                                    textAlign = TextAlign.Center,
                                    style = EateryBlueTypography.caption,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    fontWeight = FontWeight(600)
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 8.dp)
                                        .clickable { currSelectedDay = i }
                                ) {
                                    Surface(
                                        modifier = Modifier.size(size = 34.dp),
                                        color = when (i) {
                                            currSelectedDay -> EateryBlue
                                            selectedDay -> GrayFive
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    ) {}

                                    Text(
                                        text = days[i].toString(),
                                        color = if (i == currSelectedDay || i == selectedDay) Color.White else Color.Black,
                                        style = EateryBlueTypography.h6,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(bottom = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Show menu and reset menu buttons
                    Column(
                        modifier = Modifier.padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                selectedDay = currSelectedDay
                                openUpcoming = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(100),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = EateryBlue, contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Show menu",
                                style = EateryBlueTypography.h5,
                                color = Color.White
                            )
                        }
                        ClickableText(modifier = Modifier.padding(top = 12.dp),
                            text = AnnotatedString("Reset"),
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 17.5.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFF050505)
                            ),
                            onClick = { selectedDay = weekDayIndex })
                    }

                }

            }
        }
    }

    if (true) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            SearchBar(searchText = filterText,
                onSearchTextChange = { filterText = it },
                placeholderText = "Search the menu...",
                modifier = Modifier.padding(horizontal = 16.dp),
                onCancelClicked = {
                    filterText = ""
                })
            Spacer(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GrayZero, CircleShape)
            )

            event.menu?.forEach { category ->
                val filteredItems = category.items?.filter {
                    it.name?.contains(filterText, true)
//                            ?: it.description?.contains(
//                            filterText,
//                            true
//                        )
                        ?: false
                }
                if (filteredItems.isNullOrEmpty()) return@forEach

                Text(
                    text = category.category ?: "Category",
                    style = EateryBlueTypography.h5,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                filteredItems.forEachIndexed { index, menuItem ->
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row {
                            Text(
                                text = menuItem.name ?: "Item Name",
                                style = EateryBlueTypography.button,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (category.items.lastIndex != index) {
                            Spacer(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(GrayZero, CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(GrayZero, CircleShape)
        )

    }

    @Composable
    fun ReportButtonEateryDetails() {
        Surface(
            shape = RoundedCornerShape(17.dp),
            modifier = Modifier
                .height(50.dp)
                .padding(vertical = 8.dp),
            color = GrayZero,
            contentColor = Color.Black
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(ButtonDefaults.ContentPadding)
            ) {
                Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Report an Issue",
                    style = EateryBlueTypography.button,
                    color = Color.Black,
                )
            }
        }

    }
}

/**
 * Details all the possible bottom sheets for EateryDetailScreen.
 *
 * All possible bottom sheets should be added here and switched to before expanding via modalBottomSheetState.
 */
enum class BottomSheetContent {
    PAYMENT_METHODS_AVAILABLE, HOURS, WAIT_TIME, REPORT
}
