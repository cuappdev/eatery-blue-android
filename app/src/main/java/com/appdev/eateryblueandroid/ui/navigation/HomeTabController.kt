package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeTabViewModel

@Composable
fun HomeTabController(
    homeTabViewModel: HomeTabViewModel,
    homeViewModel: HomeViewModel,
    eateryDetailViewModel: EateryDetailViewModel,
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
                    scrollState = eateryListScrollState
                )
            is HomeTabViewModel.State.EateryDetailVisible ->
                EateryDetailScreen(
                    eateryDetailViewModel = eateryDetailViewModel,
                    hideEatery = homeTabViewModel::transitionEateryList
                )
        }
    }
}