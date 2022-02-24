package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import kotlin.math.exp

@Composable
fun Main(
    scrollState: LazyListState,
    sections: List<EaterySection>,
    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit,
    selectSection: (eaterySection: EaterySection) -> Unit
) {
    val mainItems: List<MainItem> = listOf(
        listOf(MainItem.SearchBox),
        listOf(MainItem.FilterOptions),
        sections.flatMap{ section -> listOf(
            MainItem.EaterySectionLabel(
                section.name,
                expandable = eateries.filter{ section.filter(it) }.size > 3,
                expandSection = {selectSection(section)}
            ),
            MainItem.EaterySectionList(section)
        ) },
        listOf(MainItem.EaterySectionLabel(
            "All Eateries",
            expandable = false, expandSection = {}
        )),
        eateries.map{MainItem.EateryItem(it)}
    ).flatten()

    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(bottom=30.dp)
    ) {
        items(mainItems) { item ->
            when (item) {
                is MainItem.SearchBox ->
                    Column(modifier = Modifier.padding(16.dp, 12.dp)) {
                        SearchBar()
                    }
                is MainItem.FilterOptions -> EateryFilters()
                is MainItem.EaterySectionLabel ->
                    Row(modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
                        .fillMaxWidth(),
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
                                clickable = item.expandable
                            )
                        }
                    }
                is MainItem.EaterySectionList ->
                        EaterySectionPreview(
                            eateries = eateries,
                            section = item.section,
                            selectSection = selectSection
                        )
                is MainItem.EateryItem ->
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                        EateryCard(eatery = item.eatery, selectEatery = selectEatery)
                    }
            }
        }
    }
}

sealed class MainItem {
    object SearchBox: MainItem()
    object FilterOptions: MainItem()
    data class EaterySectionLabel(
        val label: String,
        val expandable: Boolean,
        val expandSection: () -> Unit
    ): MainItem()
    data class EaterySectionList(val section: EaterySection): MainItem()
    data class EateryItem(val eatery: Eatery): MainItem()
}