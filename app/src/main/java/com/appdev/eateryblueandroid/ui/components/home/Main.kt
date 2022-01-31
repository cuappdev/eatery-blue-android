package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.EateryCard

@Composable
fun Main(
    scrollState: LazyListState,
    sections: List<EaterySection>,
    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit
) {

    val mainItems: List<MainItem> = listOf(
        listOf(MainItem.SearchBox),
        listOf(MainItem.FilterOptions),
        sections.map{MainItem.EaterySectionItem(it)},
        eateries.map{MainItem.EateryItem(it)}
    ).flatten()

    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
    ) {
        items(mainItems) { item ->

            when (item) {
                is MainItem.SearchBox ->
                    Column(modifier = Modifier.padding(13.dp, 5.dp)) {
                        SearchBar()
                    }
                is MainItem.FilterOptions -> EateryFilters()
                is MainItem.EaterySectionItem ->
                        EaterySectionPreview(eateries = eateries, section = item.section )
                is MainItem.EateryItem ->
                    Column(modifier = Modifier.padding(13.dp, 5.dp)) {
                        EateryCard(eatery = item.eatery, selectEatery = selectEatery)
                    }
            }
        }
    }
}

sealed class MainItem {
    object SearchBox: MainItem()
    object FilterOptions: MainItem()
    data class EaterySectionItem(val section: EaterySection): MainItem()
    data class EateryItem(val eatery: Eatery): MainItem()
}