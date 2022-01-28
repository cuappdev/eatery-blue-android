package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.sharedelements.LocalState
import com.appdev.eateryblueandroid.ui.components.sharedelements.SharedElementsRoot
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.EateryListViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeTabViewModel

@Composable
fun HomeTabController(
    homeTabViewModel: HomeTabViewModel,
    eateryListViewModel: EateryListViewModel,
    eateryDetailViewModel: EateryDetailViewModel,
    eateryListScrollState: LazyListState
) {
    val state = homeTabViewModel.state.collectAsState()

    SharedElementsRoot {
        state.value.let {
            when (it) {
                is HomeTabViewModel.State.EateryListVisible ->
                    EateryListScreen(
                        eateryListViewModel = eateryListViewModel,
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
}