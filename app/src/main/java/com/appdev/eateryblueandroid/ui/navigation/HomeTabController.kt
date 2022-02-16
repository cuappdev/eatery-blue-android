package com.appdev.eateryblueandroid.ui.screens

import android.widget.SearchView
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.viewmodels.*

@Composable
fun HomeTabController(
    homeTabViewModel: HomeTabViewModel,
    homeViewModel: HomeViewModel,
    eateryDetailViewModel: EateryDetailViewModel,
    expandedSectionViewModel: ExpandedSectionViewModel,
    searchViewModel: SearchViewModel,
    eateryListScrollState: LazyListState
) {
    val state = homeTabViewModel.state.collectAsState()

    state.value.let {
        when (it) {
            is HomeTabViewModel.State.EateryListVisible ->
                HomeScreen(
                    homeViewModel = homeViewModel,
                    selectEatery = fun(eatery: Eatery) {
                        eateryDetailViewModel.selectEatery(eatery)
                        homeTabViewModel.transitionEateryDetail()
                    },
                    selectSection = fun(section: EaterySection) {
                        expandedSectionViewModel.expandSection(section)
                        homeTabViewModel.transitionExpandedSection()
                    },
                    selectSearch = fun() {
                        searchViewModel.transitionSearchLoading()
                        homeTabViewModel.transitionSearchScreen()
                    },
                    scrollState = eateryListScrollState
                )
            is HomeTabViewModel.State.EateryDetailVisible ->
                EateryDetailScreen(
                    eateryDetailViewModel = eateryDetailViewModel,
                    hideEatery = homeTabViewModel::transitionEateryList
                )
            is HomeTabViewModel.State.ExpandedSectionVisible ->
                ExpandedSectionScreen(
                    expandedSectionViewModel = expandedSectionViewModel,
                    hideSection = homeTabViewModel::transitionEateryList
                )
            is HomeTabViewModel.State.SearchScreenVisible ->
                SearchingScreen(
                    searchViewModel = searchViewModel,
                    hideSection = homeTabViewModel::transitionEateryList
                )
        }
    }
}