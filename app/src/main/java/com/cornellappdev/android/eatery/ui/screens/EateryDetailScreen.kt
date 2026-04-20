package com.cornellappdev.android.eatery.ui.screens


import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MenuItem
import com.cornellappdev.android.eatery.data.repositories.CoilRepository
import com.cornellappdev.android.eatery.ui.components.comparemenus.CompareMenusBottomSheet
import com.cornellappdev.android.eatery.ui.components.comparemenus.CompareMenusFAB
import com.cornellappdev.android.eatery.ui.components.details.AlertsSection
import com.cornellappdev.android.eatery.ui.components.details.CalendarButton
import com.cornellappdev.android.eatery.ui.components.details.EateryDetailsStickyHeader
import com.cornellappdev.android.eatery.ui.components.details.EateryHourBottomSheet
import com.cornellappdev.android.eatery.ui.components.details.EateryMealTabs
import com.cornellappdev.android.eatery.ui.components.details.EateryMenusBottomSheet
import com.cornellappdev.android.eatery.ui.components.details.PaymentWidgets
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.components.general.NetworkErrorToast
import com.cornellappdev.android.eatery.ui.components.general.PaymentMethodsAvailable
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.general.menuItems
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.components.home.EateryDetailLoadingScreen
import com.cornellappdev.android.eatery.ui.components.settings.Issue
import com.cornellappdev.android.eatery.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.colorInterp
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.EateryDetailViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.EateryDetailViewState
import com.cornellappdev.android.eatery.ui.viewmodels.MealViewState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.ReportUiState
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import com.cornellappdev.android.eatery.util.fromOffsetToDayOfWeek
import com.cornellappdev.android.eatery.util.toMealTypeDisplayName
import com.cornellappdev.android.eatery.util.toReadableFullName
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel = hiltViewModel(),
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository(),
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
) {
    val viewState = eateryDetailViewModel.eateryDetailViewState.collectAsStateWithLifecycle().value
    val error by eateryDetailViewModel.error.collectAsStateWithLifecycle()
    val reportState by eateryDetailViewModel.reportState.collectAsStateWithLifecycle()
    val filterText by eateryDetailViewModel.searchQueryFlow.collectAsStateWithLifecycle()

    NetworkErrorToast(
        error = error,
        onErrorShown = eateryDetailViewModel::clearError
    )

    EateryDetailScreenContent(
        viewState = viewState,
        filterText = filterText,
        onCompareMenusClick = onCompareMenusClick,
        onToggleFavorite = eateryDetailViewModel::toggleFavorite,
        reportState = reportState,
        onSendReport = eateryDetailViewModel::sendReport,
        onClearReportState = eateryDetailViewModel::clearReportState,
        onSelectEvent = eateryDetailViewModel::selectEvent,
        onSetSelectedWeekdayIndex = eateryDetailViewModel::setSelectedWeekdayIndex,
        onResetSelectedEvent = eateryDetailViewModel::resetSelectedEvent,
        onSearchQueryChange = eateryDetailViewModel::setSearchQuery,
        onToggleFavoriteMenuItem = eateryDetailViewModel::toggleFavoriteMenuItem,
        onRequestRatingPopup = { appStorePopupRepository.requestRatingPopup() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EateryDetailScreenContent(
    viewState: EateryDetailViewState,
    filterText: String,
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
    onToggleFavorite: () -> Unit,
    reportState: ReportUiState,
    onSendReport: (issue: String, report: String, eateryId: Int?) -> Unit,
    onClearReportState: () -> Unit,
    onSelectEvent: (eatery: Eatery, dayIndex: Int, mealDescription: String) -> Unit,
    onSetSelectedWeekdayIndex: (Int) -> Unit,
    onResetSelectedEvent: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavoriteMenuItem: (String) -> Unit,
    onRequestRatingPopup: () -> Unit = {},
) {
    val colors = currentColors
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val context = LocalContext.current
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf(BottomSheetContent.PAYMENT_METHODS_AVAILABLE) }
    val coroutineScope = rememberCoroutineScope()
    val issue by remember { mutableStateOf<Issue?>(null) }

    var showFAB by remember {
        mutableStateOf(true)
    }

    val compareMenusScale by animateFloatAsState(
        targetValue = if (showFAB) 1.0f else 0.0f,
        label = "fab_scale"
    )

    val closeBottomSheet: () -> Unit = {
        coroutineScope.launch {
            modalBottomSheetState.hide()
            showBottomSheet = false
            onClearReportState()
        }
    }
    val openBottomSheet: (BottomSheetContent) -> Unit = { content ->
        sheetContent = content
        showFAB = false
        showBottomSheet = true
    }

    LaunchedEffect(modalBottomSheetState.isVisible, showBottomSheet) {
        if (!modalBottomSheetState.isVisible && !showBottomSheet) {
            showFAB = true
        }
    }

    Scaffold(
        floatingActionButton = {
            CompareMenusFAB(
                modifier = Modifier.scale(compareMenusScale),
            ) {
                if (!showFAB) {
                    return@CompareMenusFAB
                }

                openBottomSheet(BottomSheetContent.COMPARE_MENUS)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(currentColors.backgroundDefault)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (viewState) {
                is EateryDetailViewState.Error -> {
                    // TODO we should have an error state for this screen lol
                    Text("Cannot load Eatery Details")
                }

                is EateryDetailViewState.Loaded -> {
                    val eatery = viewState.eatery
                    val noAppAvailableText =
                        stringResource(R.string.no_app_available_to_open_this_link)
                    val noMapsAppText =
                        stringResource(R.string.no_maps_app_available_on_this_device)
                    val paymentMethods = remember(eatery) {
                        buildList {
                            if (eatery.acceptsCash()) add(PaymentMethodsAvailable.CASH)
                            if (eatery.acceptsBRB()) add(PaymentMethodsAvailable.BRB)
                            if (eatery.acceptsMealSwipes()) add(PaymentMethodsAvailable.SWIPES)
                        }
                    }
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

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = closeBottomSheet,
                            sheetState = modalBottomSheetState,
                            shape = RoundedCornerShape(
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp,
                                topStart = 12.dp,
                                topEnd = 12.dp
                            )
                        ) {
                            when (sheetContent) {
                                BottomSheetContent.PAYMENT_METHODS_AVAILABLE -> {
                                    PaymentMethodsAvailable(selectedPaymentMethods = paymentMethods) {
                                        closeBottomSheet()
                                    }
                                }

                                BottomSheetContent.REPORT -> {
                                    eatery.id?.let {
                                        ReportBottomSheet(
                                            issue = issue,
                                            eateryId = it,
                                            reportState = reportState,
                                            sendReport = onSendReport,
                                            clearReportState = onClearReportState,
                                        ) {
                                            closeBottomSheet()
                                        }
                                    }
                                }

                                BottomSheetContent.HOURS -> {
                                    EateryHourBottomSheet(
                                        onDismiss = closeBottomSheet,
                                        eatery = eatery,
                                        onReportIssue = { sheetContent = BottomSheetContent.REPORT }
                                    )
                                }

                                BottomSheetContent.MENUS -> {
                                    EateryMenusBottomSheet(
                                        weekDayIndex = viewState.weekdayIndex,
                                        onDismiss = closeBottomSheet,
                                        eatery = eatery,
                                        onShowMenuClick = { dayIndex, mealDescription, _ ->
                                            onSelectEvent(
                                                eatery,
                                                dayIndex,
                                                mealDescription
                                            )
                                            onSetSelectedWeekdayIndex(dayIndex)
                                        },
                                        onResetClick = {
                                            onSetSelectedWeekdayIndex(0)
                                            onResetSelectedEvent()
                                        },
                                        mealType = viewState.mealTypeIndex
                                    )
                                }

                                BottomSheetContent.COMPARE_MENUS -> {
                                    CompareMenusBottomSheet(
                                        onDismiss = closeBottomSheet,
                                        onCompareMenusClick = { selectedEateriesIds ->
                                            closeBottomSheet()
                                            onCompareMenusClick(selectedEateriesIds)
                                        },
                                        firstEatery = eatery
                                    )
                                }

                                else -> {}
                            }
                        }
                    }


                    val listState = rememberLazyListState()
                    val showStickyHeader by remember {
                        derivedStateOf { listState.firstVisibleItemIndex >= 1 }
                    }

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
                                                        contentDescription = null,
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
                                                                    colors.backgroundSecondary,
                                                                    colors.backgroundDefault10
                                                                )
                                                            ),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }


                                                else -> {
                                                    Image(
                                                        modifier = Modifier
                                                            .height(240.dp)
                                                            .fillMaxWidth(),
                                                        painter = painterResource(R.drawable.blank_eatery),
                                                        contentDescription = stringResource(R.string.a11y_eatery_image),
                                                        contentScale = ContentScale.Crop,
                                                    )
                                                }
                                            }
                                        }
                                    }


                                    Button(
                                        onClick = onToggleFavorite,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 40.dp, end = 16.dp)
                                            .size(40.dp),
                                        contentPadding = PaddingValues(6.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = currentColors.backgroundDefault,
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (viewState.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                            tint = if (viewState.isFavorite) colors.accentPressed else colors.textSecondary,
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
                                        openBottomSheet(BottomSheetContent.PAYMENT_METHODS_AVAILABLE)
                                    }
                                }
                            }


                            item {
                                Text(
                                    text = eatery.name ?: stringResource(R.string.loading),
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                                    style = EateryBlueTypography.h3,
                                    color = currentColors.textPrimary
                                )
                            }
                            item {
                                Text(
                                    text = if (!eatery.menuSummary.isNullOrBlank()) {
                                        stringResource(
                                            R.string.eatery_detail_location_with_summary,
                                            eatery.location.orEmpty(),
                                            eatery.menuSummary
                                        )
                                    } else {
                                        stringResource(
                                            R.string.eatery_detail_location_only,
                                            eatery.location.orEmpty()
                                        )
                                    },
                                    modifier = Modifier.padding(start = 16.dp),
                                    style = EateryBlueTypography.subtitle2,
                                    color = colors.textSecondary
                                )
                            }
                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    if (!eatery.acceptsMealSwipes()) {
                                        Button(
                                            onClick = {
                                                val getAppIntent =
                                                    context.packageManager.getLaunchIntentForPackage(
                                                        "com.cbord.get"
                                                    )
                                                val launchedGetApp = if (getAppIntent != null) {
                                                    getAppIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                                                    launchIntentSafely(context, getAppIntent)
                                                } else {
                                                    false
                                                }

                                                if (!launchedGetApp) {
                                                    val playStoreUri =
                                                        "https://play.google.com/store/apps/details?id=com.cbord.get".toUri()
                                                    val openPlayIntent =
                                                        Intent(Intent.ACTION_VIEW).apply {
                                                            data = playStoreUri
                                                            setPackage("com.android.vending")
                                                        }

                                                    val launchedPlayStore =
                                                        launchIntentSafely(context, openPlayIntent)
                                                    if (!launchedPlayStore) {
                                                        val browserIntent =
                                                            Intent(Intent.ACTION_VIEW, playStoreUri)
                                                        val launchedBrowser =
                                                            launchIntentSafely(
                                                                context,
                                                                browserIntent
                                                            )
                                                        if (!launchedBrowser) {
                                                            Log.w(
                                                                EATERY_DETAIL_TAG,
                                                                "No activity found to open GET app or Play Store listing"
                                                            )
                                                            Toast.makeText(
                                                                context,
                                                                noAppAvailableText,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(100),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colors.accentPrimary,
                                                contentColor = currentColors.backgroundDefault
                                            )
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_android_phone),
                                                contentDescription = null,
                                                tint = currentColors.textPrimary
                                            )
                                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                            Text(
                                                modifier = Modifier.padding(vertical = 6.dp),
                                                text = stringResource(R.string.order_online),
                                                style = EateryBlueTypography.h5,
                                                color = currentColors.textPrimary
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            val mapIntent =
                                                Intent(Intent.ACTION_VIEW).apply {
                                                    data =
                                                        "google.navigation:q=${eatery.latitude},${eatery.longitude}&mode=w".toUri()
                                                    setPackage("com.google.android.apps.maps")
                                                }

                                            val launchedGoogleMaps =
                                                launchIntentSafely(context, mapIntent)
                                            if (!launchedGoogleMaps) {
                                                val fallbackMapIntent =
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        "geo:${eatery.latitude},${eatery.longitude}".toUri()
                                                    )
                                                val launchedFallback =
                                                    launchIntentSafely(context, fallbackMapIntent)
                                                if (!launchedFallback) {
                                                    Log.w(
                                                        EATERY_DETAIL_TAG,
                                                        "No activity found to handle maps navigation intent"
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        noMapsAppText,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(100),
                                        modifier = if (!eatery.acceptsMealSwipes()) Modifier else Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = currentColors.accentPrimary,
                                            contentColor = currentColors.textPrimary
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_walk),
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            text = stringResource(R.string.get_directions),
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
                                            1.dp, colors.accentPrimary, RoundedCornerShape(8.dp)
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
                                                openBottomSheet(BottomSheetContent.HOURS)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.border(
                                                1.dp,
                                                color = currentColors.accentPrimary
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Schedule,
                                                contentDescription = null,
                                                tint = colors.textSecondary
                                            )
                                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                            Text(
                                                text = stringResource(R.string.hours_title),
                                                style = TextStyle(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp
                                                ), color = colors.textSecondary
                                            )
                                        }
                                        val openUntil = eatery.getOpenUntil()
                                        Text(
                                            modifier = Modifier.padding(top = 2.dp),
                                            text = when {
                                                openUntil == null -> stringResource(R.string.closed)
                                                eatery.isClosingSoon() -> stringResource(
                                                    R.string.closing_at,
                                                    openUntil
                                                )

                                                else -> stringResource(
                                                    R.string.open_until,
                                                    openUntil
                                                )
                                            },
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp
                                            ),
                                            color = if (openUntil == null) colors.error
                                            else if (eatery.isClosingSoon()) colors.accentPressed
                                            else colors.success
                                        )
                                    }

                                    HorizontalDivider(
                                        color = colors.accentPrimary,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .fillMaxHeight(0.5f)
                                            .width(1.dp)
                                    )
                                }
                            }

                            item {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(16.dp)
                                        .background(colors.accentPrimary)
                                )
                            }
                            nextEvent.menu?.let {
                                menuHeadingItem(
                                    viewState.weekdayIndex,
                                    nextEvent.toEvent(),
                                    hoursOnClick = {
                                        openBottomSheet(BottomSheetContent.MENUS)
                                    })
                                item {
                                    SearchBar(
                                        searchText = filterText,
                                        onSearchTextChange = {
                                            onSearchQueryChange(it)
                                        },
                                        placeholderText = stringResource(R.string.search_placeholder_menu),
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        onCancelClicked = {
                                            onSearchQueryChange("")
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
                                            .background(colors.accentPrimary, CircleShape)
                                    )
                                }
                                eatery.getTypeMeal(viewState.weekdayIndex.fromOffsetToDayOfWeek())
                                    .takeIf { it.size > 1 }
                                    ?.map { it.mealType.toMealTypeDisplayName() }
                                    ?.let { mealTypes ->
                                        item {
                                            EateryMealTabs(
                                                meals = mealTypes,
                                                onSelectMeal = { selectedMeal ->
                                                    onSelectEvent(
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
                                    onToggleFavoriteMenuItem(it)
                                })

                                item {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(16.dp)
                                            .background(colors.accentPrimary)
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
                                        text = stringResource(R.string.make_eatery_better),
                                        style = EateryBlueTypography.h5,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.make_eatery_better_description),
                                        style = EateryBlueTypography.body2,
                                        modifier = Modifier.padding(bottom = 5.dp),
                                        color = colors.textSecondary
                                    )


                                    Spacer(Modifier.height(8.dp))
                                    //reporting button
                                    Button(
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier
                                            .height(35.dp)
                                            .shadow(0.dp),
                                        onClick = {
                                            openBottomSheet(BottomSheetContent.REPORT)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors.backgroundDefault,
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Report,
                                            contentDescription = Icons.Default.Report.name,
                                            tint = Color.Unspecified
                                        )
                                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(
                                            text = stringResource(R.string.report_an_issue),
                                            style = EateryBlueTypography.button,
                                            color = currentColors.textPrimary
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
                                        .background(colors.backgroundDefault)
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = showStickyHeader,
                            enter = fadeIn(animationSpec = tween(100)),
                            exit = fadeOut(animationSpec = tween(100))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(currentColors.backgroundDefault)
                                    .padding(top = 48.dp, bottom = 12.dp)
                            ) {
                                EateryHeader(
                                    eatery = eatery,
                                    isFavorite = viewState.isFavorite,
                                    onFavoriteClick = onToggleFavorite
                                )
                                EateryDetailsStickyHeader(
                                    nextEvent.toEvent(),
                                    filterText,
                                    fullMenuList,
                                    listState,
                                    5,
                                    onRequestRatingPopup = onRequestRatingPopup,
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
    hoursOnClick: () -> Unit
) {
    item {
        val colors = currentColors
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
                    color= currentColors.textPrimary
                )
                if (nextEvent.startTimestamp != null && nextEvent.endTimestamp != null) {
                    Text(
                        text = "${
                            nextEvent.startTimestamp.format(
                                DateTimeFormatter.ofPattern("h:mm a")
                            )
                        } - ${
                            nextEvent.endTimestamp.format(
                                DateTimeFormatter.ofPattern("h:mm a")
                            )
                        }",
                        style = EateryBlueTypography.subtitle2,
                        color = colors.textSecondary
                    )
                }
            }
            CalendarButton(onClick = hoursOnClick)
        }
    }
}

@Composable
fun EateryHeader(eatery: Eatery, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    val colors = currentColors
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
            text = eatery.name ?: stringResource(R.string.loading),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = currentColors.textPrimary,
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
                containerColor = currentColors.backgroundDefault,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                tint = if (isFavorite) colors.accentPressed else colors.textSecondary,
                contentDescription = null
            )
        }
    }
}

private fun launchIntentSafely(context: android.content.Context, intent: Intent): Boolean {
    val packageManager = context.packageManager
    return if (intent.resolveActivity(packageManager) != null) {
        try {
            context.startActivity(intent)
            true
        } catch (exception: ActivityNotFoundException) {
            Log.w(EATERY_DETAIL_TAG, "Unable to start activity for intent: $intent", exception)
            false
        }
    } else {
        false
    }
}

private const val EATERY_DETAIL_TAG = "EateryDetailScreen"

@Preview(showBackground = true)
@Composable
private fun EateryDetailScreenPreview() = EateryPreview {
    val now = LocalDateTime.now()
    val mealViewState = MealViewState(
        startTime = now.withHour(11).withMinute(0),
        endTime = now.withHour(14).withMinute(0),
        menu = listOf(
            MenuCategoryViewState(
                category = "Featured",
                items = listOf(
                    MenuItemViewState(
                        isFavorite = true,
                        item = MenuItem(name = "Pesto Pasta")
                    ),
                    MenuItemViewState(
                        isFavorite = false,
                        item = MenuItem(name = "Tomato Soup")
                    )
                )
            )
        ),
        description = "Lunch"
    )

    EateryDetailScreenContent(
        viewState = EateryDetailViewState.Loaded(
            mealToShow = mealViewState,
            eatery = PreviewData.mockEatery(id = 1).copy(
                menuSummary = "Pasta and grill"
            ),
            isFavorite = true,
            weekdayIndex = 0
        ),
        filterText = "",
        onCompareMenusClick = {},
        onToggleFavorite = {},
        reportState = ReportUiState.Idle,
        onSendReport = { _, _, _ -> },
        onClearReportState = {},
        onSelectEvent = { _, _, _ -> },
        onSetSelectedWeekdayIndex = {},
        onResetSelectedEvent = {},
        onSearchQueryChange = {},
        onToggleFavoriteMenuItem = {},
    )
}
