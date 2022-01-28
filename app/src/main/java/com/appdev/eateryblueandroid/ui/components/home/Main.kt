package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.clickable
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
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModelItem

@Composable
fun Main(
    scrollState: LazyListState,
    homeItems: List<HomeViewModelItem>,
    selectEatery: (eatery: Eatery) -> Unit
) {
    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp)
    ) {
        items(homeItems) { item ->
            Column(modifier = Modifier.padding(0.dp, 5.dp)) {
                when (item) {
                    is HomeViewModelItem.SearchBox -> SearchBar()
                    is HomeViewModelItem.EateryCategory ->
                        CategorySection(name = item.name, eateries = item.eateries )
                    is HomeViewModelItem.EateryItem ->
                        EateryCard(eatery = item.eatery, selectEatery = selectEatery)
                }
            }
        }
    }
}