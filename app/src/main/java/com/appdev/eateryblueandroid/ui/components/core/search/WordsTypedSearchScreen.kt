package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.home.EateryFilters
import com.appdev.eateryblueandroid.ui.components.home.MainItem
import com.appdev.eateryblueandroid.ui.components.home.SearchBar
import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel

@Composable
fun WordsTypedSearchScreen(
//    scrollState: LazyListState,
    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit,
    searchViewModel: SearchViewModel,

){
    val typedText = searchViewModel.typedText.value
    var filterEatery = mutableListOf<Eatery>()
    eateries.forEach{
        eatery ->
        var eateryName = eatery.name?.lowercase()
        if(eateryName?.startsWith(typedText.lowercase() , true) == true){
            filterEatery.add(eatery)
        }
    }
       val searchTextedItem: List<SearchTextedItem> = listOf(
//        listOf(SearchTextedItem.SearchBox),
        listOf(SearchTextedItem.FilterOptions),
           filterEatery.map{ SearchTextedItem.EateryItem(it)}
    ).flatten()


    LazyColumn(
        contentPadding = PaddingValues(bottom=30.dp)
    ) {
        items(searchTextedItem) { item ->
            when (item) {
//                is SearchTextedItem.SearchBox ->
//
//                        TypeableSearchBar(searchViewModel)
//                    }
                is SearchTextedItem.FilterOptions -> Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    EateryFilters()
                }
                is SearchTextedItem.EateryItem ->
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                        EateryCard(eatery = item.eatery, selectEatery = selectEatery)
                    }
                }
            }
        }
    }


sealed class SearchTextedItem {
//    object SearchBox: SearchTextedItem()
    object FilterOptions: SearchTextedItem()
    data class EateryItem(val eatery: Eatery): SearchTextedItem()
}