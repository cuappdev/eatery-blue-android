package com.appdev.eateryblueandroid.ui.components.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.models.getWalkTimes
import com.appdev.eateryblueandroid.models.isClosed
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.util.Constants.WORLD_DISTANCE_KM

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Main(
    scrollState: LazyListState,
    sections: List<EaterySection>,
    eateries: List<Eatery>,
    filters: List<String>,
    setFilters: (selection: List<String>) -> Unit,
    selectEatery: (eatery: Eatery) -> Unit,
    selectSection: (eaterySection: EaterySection) -> Unit,
    selectSearch: () -> Unit,
    bottomSheetViewModel: BottomSheetViewModel
) {
    var mainItems by remember { mutableStateOf(listOf<MainItem>()) }
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val showBottomSheet = {
        val updatedFilter = mutableStateOf(filters)
        val toggleFilter = { selected: String ->
            if (updatedFilter.value.contains(selected)) {
                updatedFilter.value = updatedFilter.value.filter { it != selected }
            } else {
                updatedFilter.value = updatedFilter.value + selected
            }
        }
        bottomSheetViewModel.show {
            PaymentMethodFilter(
                selectedFilter = updatedFilter,
                toggleFilter = toggleFilter,
                saveFilter = setFilters,
                hide = bottomSheetViewModel::hide
            )
        }
    }
    if (filters.contains("Payment Options")) {
        showBottomSheet()
    }
    Log.d("mainItemsDebug", filters.toString())

    val eateriesFiltered = eateries.filter {
        var isContained = true
        if (filters.contains("North")) isContained =
            isContained && it.campusArea == "North"
        if (filters.contains("West")) isContained =
            isContained && it.campusArea == "West"
        if (filters.contains("Central")) isContained =
            isContained && it.campusArea == "Central"
        if (filters.contains("Under 10 minutes")) isContained =
            isContained && getWalkTimes(it) < 10
        if (filters.contains("Favorites")) isContained =
            isContained && it.isFavorite()
        isContained = isContained &&
            ((filters.contains("Meal swipes") && it.paymentAcceptsMealSwipes == true) ||
                (filters.contains("BRBs") && it.paymentAcceptsBrbs == true) ||
                (filters.contains("Cash or credit") && it.paymentAcceptsCash == true))
        isContained
    }.sortedByDescending {
        if (isClosed(it)) 0 else WORLD_DISTANCE_KM - getWalkTimes(
            it
        )
    }.map { MainItem.EateryItem(it) }

    mainItems = listOf(
        listOf(MainItem.SearchBox),
        listOf(MainItem.FilterOptions),
        // If no filters are applied...
        if (filters.toSet() == setOf("BRBs", "Meal swipes", "Cash or credit"))
            sections.filter { section -> eateries.any { section.filter(it) } }
                .flatMap { section ->
                    listOf(
                        MainItem.EaterySectionLabel(
                            section.name,
                            expandable = eateries.filter { section.filter(it) }.size > 3,
                            expandSection = { selectSection(section) }
                        ),
                        MainItem.EaterySectionList(section)
                    )
                }
        else listOf(),
        if (filters.toSet() == setOf("BRBs", "Meal swipes", "Cash or credit"))
            listOf(
                MainItem.EaterySectionLabel(
                    "All Eateries",
                    expandable = false, expandSection = {}
                ))
        else listOf(MainItem.EaterySpacer),
        eateriesFiltered,
        if (eateriesFiltered.isEmpty()) listOf(MainItem.EmptyMessage)
        else listOf()
    ).flatten()

    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        items(mainItems, key = { it.hashCode() }) { item ->
            when (item) {
                is MainItem.SearchBox ->
                    Column(modifier = Modifier.padding(16.dp, 12.dp)) {
                        SearchBar(selectSearch = selectSearch)
                    }
                is MainItem.FilterOptions ->
                    EateryFilters(alreadySelected = filters) {
                        setFilters(it)
                    }
                is MainItem.EaterySectionLabel ->
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.label,
                            textStyle = TextStyle.HEADER_H3
                        )
                        if (item.expandable) {
                            CircularBackgroundIcon(
                                icon = painterResource(
                                    id = R.drawable.ic_rightarrow
                                ),
                                onTap = item.expandSection,
                                clickable = item.expandable,
                            )
                        }
                    }
                is MainItem.EaterySectionList ->
                    Box(modifier = Modifier.animateItemPlacement()) {
                        EaterySectionPreview(
                            eateries = eateries,
                            section = item.section,
                            selectSection = selectSection,
                            selectEatery = selectEatery
                        )
                    }
                is MainItem.EateryItem ->
                    Column(
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 12.dp
                            )
                            .animateItemPlacement()
                    ) {
                        EateryCard(eatery = item.eatery, selectEatery = selectEatery)
                    }
                is MainItem.EaterySpacer ->
                    Spacer(modifier = Modifier.height(12.dp))
                is MainItem.EmptyMessage ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = ((screenHeight - 311 - 157) / 2).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    )
                    {
                        Icon(
                            painter = painterResource(R.drawable.ic_eaterylogo),
                            contentDescription = null,
                            modifier = Modifier
                                .height(72.dp)
                                .width(72.dp),
                            tint = colorResource(R.color.gray02)
                        )
                        Text(
                            text = "No eatery found...",
                            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Button(
                            modifier = Modifier.padding(top = 12.dp),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                            ),
                            shape = RoundedCornerShape(100.dp),
                            colors = buttonColors(
                                backgroundColor = colorResource(R.color.eateryBlue)
                            ),
                            onClick = {
                                setFilters(listOf("BRBs", "Meal swipes", "Cash or credit"))
                            }
                        ) {
                            Text(
                                text = "Reset filters",
                                textStyle = TextStyle.BODY_SEMIBOLD,
                                color = colorResource(R.color.white),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
        }
    }
}

sealed class MainItem {
    object SearchBox : MainItem()
    object FilterOptions : MainItem()
    data class EaterySectionLabel(
        val label: String,
        val expandable: Boolean,
        val expandSection: () -> Unit
    ) : MainItem()

    data class EaterySectionList(val section: EaterySection) : MainItem()
    object EaterySpacer : MainItem()
    object EmptyMessage : MainItem()
    data class EateryItem(val eatery: Eatery) : MainItem()
}