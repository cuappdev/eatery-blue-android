package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MenuItem
import com.cornellappdev.android.eatery.ui.components.details.EateryHourBottomSheet
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eatery.ui.components.settings.Issue
import com.cornellappdev.android.eatery.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.ui.theme.Yellow
import com.cornellappdev.android.eatery.ui.viewmodels.CompareMenusViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun CompareMenusScreen(
    eateryIds: List<Int>,
    compareMenusViewModel: CompareMenusViewModel = hiltViewModel()
) {
    //todo i think this is bad practice? check how to do this via saved state handles
    compareMenusViewModel.openEatery(eateryIds)

    val eateriesApiResponse by compareMenusViewModel.eateryFlow.collectAsState()
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val coroutineScope = rememberCoroutineScope()

    var sheetContent by remember { mutableStateOf(BottomSheetContent.HOURS) }

    val issue by remember { mutableStateOf<Issue?>(null) }

    Column {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Compare Menus",
                fontSize = 20.sp,
                style = EateryBlueTypography.h5,
                fontWeight = FontWeight(600)
            )
        }
        Divider(
            color = GrayZero,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
        when (eateriesApiResponse) {
            is EateryApiResponse.Success -> {
                val eateries = (eateriesApiResponse as EateryApiResponse.Success<List<Eatery>>).data
                val pagerState = rememberPagerState()
                ModalBottomSheetLayout(
                    sheetState = modalBottomSheetState, sheetContent = {
                        when (sheetContent) {
                            BottomSheetContent.HOURS -> EateryHourBottomSheet(onDismiss = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }, eatery = eateries[pagerState.currentPage], onReportIssue = {
                                sheetContent = BottomSheetContent.REPORT
                            })

                            BottomSheetContent.REPORT -> {
                                eateries[0].id?.let {
                                    ReportBottomSheet(issue = issue,
                                        eateryid = it,
                                        sendReport = { issue, report, eateryid ->
                                            compareMenusViewModel.sendReport(
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

                            else -> {}
                        }
                    }
                ) {
                    HorizontalPager(
                        pageCount = eateries.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Column {
                            eateries[page].name?.let { Text(text = it) }
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
                                                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                            ), color = GrayFive
                                        )
                                    }
                                    val openUntil = eateries[page].getOpenUntil()
                                    Text(
                                        modifier = Modifier.padding(top = 2.dp),
                                        text =
                                        if (openUntil == null) "Closed"
                                        else if (eateries[page].isClosingSoon()) "Closing at $openUntil"
                                        else ("Open until $openUntil"),
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                        ),
                                        color = if (openUntil == null) Red
                                        else if (eateries[page].isClosingSoon()) Yellow
                                        else Green
                                    )
                                }
                            }
                            val currentEvent by remember { mutableStateOf(eateries[page].getCurrentEvent()) }
                            if (currentEvent != null) {
                                LazyColumn {
                                    currentEvent!!.menu?.forEach { category ->
                                        stickyHeader {
                                            Text(
                                                text = category.category ?: "Category",
                                                style = EateryBlueTypography.h5,
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).background(
                                                    Color.LightGray)
                                            )
                                        }
                                        itemsIndexed(category.items as List<MenuItem>) { index, menuItem ->
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)) {
                                                    Text(
                                                        text = menuItem.name ?: "Item Name",
                                                        style = EateryBlueTypography.button,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                                if (index != category.items.lastIndex) {
                                                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(GrayZero, CircleShape))
                                                }
                                            }
                                        }
//                                        if (category != currentEvent!!.menu.last()) {
//                                            Divider(color = GrayZero, modifier = Modifier.height(10.dp))
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }
}