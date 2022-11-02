package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.home.EateryFilters
import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel
import com.appdev.eateryblueandroid.util.RecentSearchesRepository
import com.appdev.eateryblueandroid.util.logSearch

/**
 * The screen where the search bar has text inside and it will auto search as words are being typed
 * into the search bar
 */
@Composable
fun WordsTypedSearchScreen(
    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit,
    searchViewModel: SearchViewModel,
    filters: List<String>,
    setFilters: (selection: List<String>) -> Unit,
) {
    val typedText = searchViewModel.typedText.value

    val filterState = rememberLazyListState()

    // create a list of the filtered eateries when the words are typed
    val filterEatery = mutableListOf<Eatery>()
    eateries.forEach { eatery ->
        val eateryName = eatery.name?.lowercase()
        if (eateryName?.startsWith(typedText.lowercase(), true) == true) {
            filterEatery.add(eatery)
        }
    }
    val searchTextedItem: List<SearchTextedItem> = listOf(
        listOf(SearchTextedItem.FilterOptions),
        filterEatery.map { SearchTextedItem.EateryItem(it) }
    ).flatten()

    // New select eatery function that also adds eatery into the recently saved list
    val selectEaterySave = fun(eatery: Eatery) {
        eatery.id?.let { RecentSearchesRepository.saveRecentSearch(it) }
        selectEatery(eatery)
        logSearch(eatery.id ?: -1)
    }
    LazyColumn(
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        items(searchTextedItem) { item ->
            when (item) {
                is SearchTextedItem.FilterOptions -> Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    EateryFilters(alreadySelected = filters, scrollState = filterState) {
                        setFilters(it)
                    }
                }
                is SearchTextedItem.EateryItem ->
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                    ) {
                        EateryCard(
                            eatery = item.eatery,
                            selectEatery = selectEaterySave
                        )
                    }
            }
        }
    }
}

sealed class SearchTextedItem {
    object FilterOptions : SearchTextedItem()
    data class EateryItem(val eatery: Eatery) : SearchTextedItem()
}