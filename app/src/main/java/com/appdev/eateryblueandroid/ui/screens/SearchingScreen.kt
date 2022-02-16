package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ExpandedSectionViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel


@Composable
fun SearchingScreen(
    searchViewModel: SearchViewModel,
    hideSection: () -> Unit
){
    val state = searchViewModel.state.collectAsState()
    state.value.let {
        when (it) {
            is SearchViewModel.State.Loading ->
                Text("Loading")
            is SearchViewModel.State.NothingTyped ->
                Text("Nothing Typed")
            is SearchViewModel.State.WordsTyped ->
                Text("Word Typed")
            is SearchViewModel.State.SearchResults ->
                Text("Search Results")
        }
    }
    BackHandler {
        hideSection()
    }
}