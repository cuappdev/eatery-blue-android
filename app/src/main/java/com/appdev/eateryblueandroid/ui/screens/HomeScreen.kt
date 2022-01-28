package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import androidx.compose.foundation.lazy.LazyListState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.home.Main

@Composable

fun HomeScreen(
    homeViewModel: HomeViewModel,
    selectEatery: (eatery: Eatery) -> Unit,
    scrollState: LazyListState
){
    val state = homeViewModel.state.collectAsState()
    state.value.let {
        when(it) {
            is HomeViewModel.State.Loading ->
                Text(text = "gucci")
            is HomeViewModel.State.Data ->
                Main(
                    scrollState = scrollState,
                    eateries = it.eateries,
                    sections = it.sections,
                    selectEatery = selectEatery
                )
            is HomeViewModel.State.Failure ->
                Text("FAILURE")
        }
    }
}