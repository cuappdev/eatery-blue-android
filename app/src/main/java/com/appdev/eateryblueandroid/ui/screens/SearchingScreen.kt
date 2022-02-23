package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.core.Text
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
    hideSection: () -> Unit
){
    val state = searchViewModel.state.collectAsState()

    val eateries = ArrayList<Eatery>()

//    //collect the eateries list
//    state.value.let{
//        when(it){
//            SearchViewModel.State.NothingTyped ->{
//
//            }
//        }
//    }




    state.value.let {
        when (it) {
            is SearchViewModel.State.Loading ->
                Text("Loading")
            is SearchViewModel.State.NothingTyped ->
                Text("Nothing Typed")
            is SearchViewModel.State.WordsTyped ->
                WordsTypedSearchScreen(
                    eateries = it.eateries,
                    selectEatery = selectEatery
                )
            is SearchViewModel.State.SearchResults ->
                Text("Search Results")
            is SearchViewModel.State.Failure ->
                Text("Failed to load")
        }
    }
    BackHandler {
        hideSection()
    }
}
sealed class SearchTextedItem {
    object SearchBox: SearchTextedItem()
    object FilterOptions: SearchTextedItem()
    data class EateryItem(val eatery: Eatery): SearchTextedItem()
}