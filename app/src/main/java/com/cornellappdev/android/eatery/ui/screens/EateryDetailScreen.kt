package com.cornellappdev.android.eatery.ui.screens


import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.repositories.CoilRepository
import com.cornellappdev.android.eatery.ui.components.comparemenus.CompareMenusBotSheet
import com.cornellappdev.android.eatery.ui.components.comparemenus.CompareMenusFAB
import com.cornellappdev.android.eatery.ui.components.details.AlertsSection
import com.cornellappdev.android.eatery.ui.components.details.CalendarButton
import com.cornellappdev.android.eatery.ui.components.details.EateryDetailsStickyHeader
import com.cornellappdev.android.eatery.ui.components.details.EateryHourBottomSheet
import com.cornellappdev.android.eatery.ui.components.details.EateryMealTabs
import com.cornellappdev.android.eatery.ui.components.details.EateryMenusBottomSheet
import com.cornellappdev.android.eatery.ui.components.details.PaymentWidgets
import com.cornellappdev.android.eatery.ui.components.general.PaymentMethodsAvailable
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.general.menuItems
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.components.home.EateryDetailLoadingScreen
import com.cornellappdev.android.eatery.ui.components.settings.Issue
import com.cornellappdev.android.eatery.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayOne
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.ui.theme.Yellow
import com.cornellappdev.android.eatery.ui.theme.colorInterp
import com.cornellappdev.android.eatery.ui.viewmodels.EateryDetailViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.EateryDetailViewState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import com.cornellappdev.android.eatery.util.fromOffsetToDayOfWeek
import com.cornellappdev.android.eatery.util.toReadableFullName
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel = hiltViewModel(),
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository(),
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val context = LocalContext.current
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    var sheetContent by remember { mutableStateOf(BottomSheetContent.PAYMENT_METHODS_AVAILABLE) }
    val paymentMethods = remember { mutableStateListOf<PaymentMethodsAvailable>() }
    val coroutineScope = rememberCoroutineScope()
    val issue by remember { mutableStateOf<Issue?>(null) }
    val viewState = eateryDetailViewModel.eateryDetailViewState.collectAsState().value


    /**
     * The amount of days offset from the current weekday
     */

    // The filter text typed in.
    val filterText by eateryDetailViewModel.searchQueryFlow.collectAsState()

    var showFAB by remember {
        mutableStateOf(true)
    }

    val compareMenusScale by animateFloatAsState(
        targetValue = if (showFAB) 1.0f else 0.0f,
        label = "fab_scale"
    )

    LaunchedEffect(modalBottomSheetState.currentValue) {
        if (modalBottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            showFAB = true
        }
    }

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            CompareMenusFAB(
                modifier = Modifier.scale(compareMenusScale),
            ) {
                if (!showFAB) {
                    return@CompareMenusFAB
                }

                showFAB = false
                coroutineScope.launch {
                    sheetContent = BottomSheetContent.COMPARE_MENUS
                    modalBottomSheetState.show()
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(paddingValues)
        ) {
            when (viewState) {
                is EateryDetailViewState.Error -> {
                    // TODO we should have an error state for this screen lol
                    Text("Cannot load Eatery Details")
                }

                is EateryDetailViewState.Loaded -> {
                    val eatery = viewState.eatery
                    val bitmapState =
                        eatery.imageUrl?.let {
                            CoilRepository.getUrlState(
                                it,
                                LocalContext.current
                            )
                        }
                    val nextEvent = viewState.mealToShow
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
                                                eateryDetailViewModel.sendReport(
                                                    issue,
                                                    report,
                                                    eateryid
                                                )
                                            }) {
                                            coroutineScope.launch {
                                                modalBottomSheetState.hide()
                                            }
                                        }
                                    }
                                }


                                BottomSheetContent.HOURS -> {
                                    EateryHourBottomSheet(onDismiss = {
                                        coroutineScope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                    }, eatery = eatery, onReportIssue = {
                                        sheetContent = BottomSheetContent.REPORT


                                    })
                                }


                                BottomSheetContent.MENUS -> {
                                    EateryMenusBottomSheet(
                                        weekDayIndex = viewState.weekdayIndex,
                                        onDismiss = {
                                            coroutineScope.launch {
                                                modalBottomSheetState.hide()
                                            }
                                        },
                                        eatery = eatery,
                                        onShowMenuClick = { dayIndex, mealDescription, _ ->
                                            eateryDetailViewModel.selectEvent(
                                                eatery,
                                                dayIndex,
                                                mealDescription
                                            )
                                            eateryDetailViewModel.setSelectedWeekdayIndex(dayIndex)
                                        },
                                        onResetClick = {
                                            eateryDetailViewModel.setSelectedWeekdayIndex(0)
                                            eateryDetailViewModel.resetSelectedEvent()
                                        },
                                        mealType = viewState.mealTypeIndex
                                    )
                                }

                                BottomSheetContent.COMPARE_MENUS -> {
                                    CompareMenusBotSheet(
                                        onDismiss = {
                                            coroutineScope.launch {
                                                modalBottomSheetState.hide()
                                            }
                                        },
                                        onCompareMenusClick = { selectedEateriesIds ->
                                            coroutineScope.launch {
                                                modalBottomSheetState.hide()
                                            }
                                            onCompareMenusClick(selectedEateriesIds)
                                        },
                                        firstEatery = eatery
                                    )
                                }


                                else -> {}
                            }
                        }, sheetShape = RoundedCornerShape(
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp,
                            topStart = 12.dp,
                            topEnd = 12.dp
                        ), sheetElevation = 8.dp
                    ) {


                        paymentMethods.apply {
                            if (eatery.paymentAcceptsCash == true) add(PaymentMethodsAvailable.CASH)
                            if (eatery.paymentAcceptsBrbs == true) add(PaymentMethodsAvailable.BRB)
                            if (eatery.paymentAcceptsMealSwipes == true) add(
                                PaymentMethodsAvailable.SWIPES
                            )
                        }


                        val listState = rememberLazyListState()


                        Box {
                            val fullMenuList: MutableList<String> =
                                nextEvent.menu?.flatMap { category ->
                                    listOf(category.category) + category.items.mapNotNull { it.item.name }
                                }?.toMutableList() ?: emptyList<String>().toMutableList()


                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
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
                                                            bitmap = ImageBitmap(
                                                                width = 1,
                                                                height = 1
                                                            ),
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
                                                imageVector = if (viewState.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                                tint = if (viewState.isFavorite) Yellow else GrayFive,
                                                contentDescription = null
                                            )
                                        }
                                        PaymentWidgets(
                                            eatery,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(16.dp)
                                                .height(40.dp)
                                        ) {
                                            sheetContent =
                                                BottomSheetContent.PAYMENT_METHODS_AVAILABLE
                                            coroutineScope.launch {
                                                modalBottomSheetState.show()
                                            }
                                        }
                                    }
                                }


                                item {
                                    Text(
                                        text = eatery.name ?: "Loading...",
                                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                                        style = EateryBlueTypography.h3,
                                    )
                                }
                                item {
                                    Text(
                                        text = "${eatery.location} ${if (!eatery.menuSummary.isNullOrBlank()) "· ${eatery.menuSummary}" else ""}",
                                        modifier = Modifier.padding(start = 16.dp),
                                        style = EateryBlueTypography.subtitle2,
                                        color = GrayFive
                                    )
                                }
                                item {
                                    Row(
                                        modifier = Modifier
                                            .padding(top = 12.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        if (eatery.paymentAcceptsMealSwipes == false) {
                                            Button(
                                                onClick = {
                                                    val getAppIntent =
                                                        context.packageManager.getLaunchIntentForPackage(
                                                            "com.cbord.get"
                                                        )
                                                    if (getAppIntent != null) {
                                                        getAppIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                                                        context.startActivity(getAppIntent)
                                                    } else {
                                                        val openPlayIntent =
                                                            Intent(Intent.ACTION_VIEW).apply {
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
                                                    backgroundColor = EateryBlue,
                                                    contentColor = Color.White
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
                                        }
                                        Button(
                                            onClick = {
                                                val mapIntent =
                                                    Intent(Intent.ACTION_VIEW).apply {
                                                        data =
                                                            Uri.parse("google.navigation:q=${eatery.latitude},${eatery.longitude}&mode=w")
                                                        setPackage("com.google.android.apps.maps")
                                                    }
                                                context.startActivity(mapIntent)
                                            },
                                            shape = RoundedCornerShape(100),
                                            modifier = if (eatery.paymentAcceptsMealSwipes == false) Modifier else Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = GrayZero,
                                                contentColor = Color.Black
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
                                }


                                item {
                                    AlertsSection(eatery = eatery)


                                    Row(
                                        modifier = Modifier
                                            .height(IntrinsicSize.Min)
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
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
                                                .clickable {
                                                    sheetContent = BottomSheetContent.HOURS
                                                    coroutineScope.launch {
                                                        modalBottomSheetState.show()
                                                    }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Schedule,
                                                    contentDescription = "Hours Icon",
                                                    tint = GrayFive
                                                )
                                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                                Text(
                                                    text = "Hours", style = TextStyle(
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 16.sp
                                                    ), color = GrayFive
                                                )
                                            }
                                            val openUntil = eatery.getOpenUntil()
                                            Text(
                                                modifier = Modifier.padding(top = 2.dp),
                                                text =
                                                if (openUntil == null) "Closed"
                                                else if (eatery.isClosingSoon()) "Closing at $openUntil"
                                                else ("Open until $openUntil"),
                                                style = TextStyle(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp
                                                ),
                                                color = if (openUntil == null) Red
                                                else if (eatery.isClosingSoon()) Yellow
                                                else Green
                                            )
                                        }


                                        Divider(
                                            color = GrayZero,
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .fillMaxHeight(0.5f)
                                                .width(1.dp)
                                        )
                                        //todo get rid of this?


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
                                }


                                item {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(16.dp)
                                            .background(GrayZero)
                                    )
                                }
                                nextEvent.menu?.let {
                                    menuHeadingItem(
                                        viewState.weekdayIndex,
                                        nextEvent.toEvent(),
                                        hoursOnClick = {
                                            sheetContent = BottomSheetContent.MENUS
                                            coroutineScope.launch {
                                                modalBottomSheetState.show()
                                            }
                                        })
                                    item {
                                        SearchBar(searchText = filterText,
                                            onSearchTextChange = {
                                                eateryDetailViewModel.setSearchQuery(
                                                    it
                                                )
                                            },
                                            placeholderText = "Search the menu...",
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            onCancelClicked = {
                                                eateryDetailViewModel.setSearchQuery("")
                                            })
                                        Spacer(
                                            modifier = Modifier
                                                .padding(
                                                    start = 16.dp,
                                                    end = 16.dp,
                                                    top = 12.dp,
                                                    bottom = 8.dp
                                                )
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(GrayZero, CircleShape)
                                        )
                                    }
                                    eatery.getTypeMeal(viewState.weekdayIndex.fromOffsetToDayOfWeek())
                                        .takeIf { it?.size?.let { s -> s > 1 } == true }
                                        ?.map { it.first }
                                        ?.let { mealTypes ->
                                            item {
                                                EateryMealTabs(
                                                    meals = mealTypes,
                                                    onSelectMeal = { selectedMeal ->
                                                        eateryDetailViewModel.selectEvent(
                                                            eatery,
                                                            viewState.weekdayIndex,
                                                            mealTypes[selectedMeal]
                                                        )
                                                    },
                                                    selectedMealIndex = viewState.mealTypeIndex
                                                )
                                            }
                                        }

                                    menuItems(nextEvent.menu.map {
                                        it.copy(
                                            items = it.items.filter { menuItem ->
                                                menuItem.item.name?.contains(filterText, true)
                                                    ?: false
                                            }
                                        )
                                    }, onFavoriteClick = {
                                        eateryDetailViewModel.toggleFavoriteMenuItem(it)
                                    })

                                    item {
                                        Spacer(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(16.dp)
                                                .background(GrayZero)
                                        )
                                    }
                                }


                                // Report an issue button
                                item {
                                    Column(
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 16.dp
                                        )
                                    ) {
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
                                            Icon(
                                                imageVector = Icons.Default.Report,
                                                Icons.Default.Report.name
                                            )
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text(
                                                text = "Report an Issue",
                                                style = EateryBlueTypography.button,
                                                color = Color.Black
                                            )
                                        }


                                        Spacer(Modifier.height(8.dp))


                                    }
                                }


                                item {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(16.dp)
                                            .background(GrayZero)
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = listState.firstVisibleItemIndex >= 1,
                                enter = fadeIn(animationSpec = tween(100)),
                                exit = fadeOut(animationSpec = tween(100))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(top = 48.dp, bottom = 12.dp)
                                ) {
                                    EateryHeader(
                                        eatery = eatery,
                                        isFavorite = viewState.isFavorite,
                                        onFavoriteClick = eateryDetailViewModel::toggleFavorite
                                    )
                                    EateryDetailsStickyHeader(
                                        nextEvent.toEvent(),
                                        eatery,
                                        filterText,
                                        fullMenuList,
                                        listState,
                                        5,
                                        onItemClick = { index ->
                                            // The first category title has an item index of 8
                                            // ideal is listState.animateScrollToItem(index + 8, scrollOffset = -400)
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(
                                                    index + 5
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                EateryDetailViewState.Loading -> {
                    EateryDetailLoadingScreen(shimmer)
                }
            }
        }
    }
}

private fun LazyListScope.menuHeadingItem(
    weekDayIndex: Int,
    nextEvent: Event,
    hoursOnClick: () -> Job
) {
    item {
        Row(
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 8.dp,
                start = 16.dp,
                end = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = weekDayIndex.fromOffsetToDayOfWeek()
                        .toReadableFullName(),
                    style = EateryBlueTypography.h4,
                )
                if (nextEvent.startTime != null && nextEvent.endTime != null) {
                    Text(
                        text = "${
                            nextEvent.startTime.format(
                                DateTimeFormatter.ofPattern("h:mm a")
                            )
                        } - ${
                            nextEvent.endTime.format(
                                DateTimeFormatter.ofPattern("h:mm a")
                            )
                        }",
                        style = EateryBlueTypography.subtitle2,
                        color = GrayFive
                    )
                }
            }
            CalendarButton(onClick = { hoursOnClick() })
        }
    }
}

@Composable
fun EateryHeader(eatery: Eatery, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Text(
            modifier = Modifier
                .height(26.dp)
                .widthIn(0.dp, 280.dp)
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = eatery.name ?: "Loading...",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Black,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        )

        Button(
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(40.dp),
            contentPadding = PaddingValues(6.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                tint = if (isFavorite) Yellow else GrayFive,
                contentDescription = null
            )
        }
    }
}
