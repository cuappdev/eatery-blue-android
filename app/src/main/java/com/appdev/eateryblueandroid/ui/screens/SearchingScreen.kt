package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.search.NoWordsTypedSearchScreen
import com.appdev.eateryblueandroid.ui.components.core.search.TypeableSearchBar
import com.appdev.eateryblueandroid.ui.components.core.search.WordsTypedSearchScreen
import com.appdev.eateryblueandroid.ui.components.home.MainItem
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ExpandedSectionViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel
import kotlinx.coroutines.selects.select


@Composable
fun SearchingScreen(
    searchViewModel: SearchViewModel,
    selectEatery: (eatery: Eatery) -> Unit,
    hideSection: () -> Unit,
    homeViewModel: HomeViewModel,
    selectSection: (eaterySection: EaterySection) -> Unit,
) {
    val state = searchViewModel.state.collectAsState()
    val homeState = homeViewModel.state.collectAsState()
    var eateries: List<Eatery> = listOf()
    var filters: List<String> = listOf()

    var failedToLoadEatery = false;
    //collect the eateries list
    homeState.value.let {
        when (it) {
            is HomeViewModel.State.Loading ->
                failedToLoadEatery = true
            is HomeViewModel.State.Data -> {
                eateries = it.eateries
                filters = it.filters
            }
            is HomeViewModel.State.Failure ->
                failedToLoadEatery = true
        }
    }
    Column() {
        TypeableSearchBar(searchViewModel)
        state.value.let {
            when (it) {
                is SearchViewModel.State.Loading ->
                    Text("Loading")
                is SearchViewModel.State.NothingTyped ->
                    NoWordsTypedSearchScreen(
                        eateries = eateries,
                        selectEatery = selectEatery,
                        searchViewModel = searchViewModel,
                        homeViewModel = homeViewModel,
                        selectSection = selectSection,
                    )
                is SearchViewModel.State.WordsTyped ->
                    WordsTypedSearchScreen(
                        eateries = eateries,
                        selectEatery = selectEatery,
                        searchViewModel = searchViewModel,
                        filters = filters,
                        setFilters = { s -> homeViewModel.updateFilters(s) },
                    )
                is SearchViewModel.State.Failure ->
                    Text("Failed to load")
            }
        }
    }
    BackHandler {
        hideSection()
    }
}

sealed class SearchTextedItem {
    object SearchBox : SearchTextedItem()
    object FilterOptions : SearchTextedItem()
    data class EateryItem(val eatery: Eatery) : SearchTextedItem()
}