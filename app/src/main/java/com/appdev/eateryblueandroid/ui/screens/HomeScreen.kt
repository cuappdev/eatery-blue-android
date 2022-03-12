package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.res.painterResource
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.general.TopBar
import com.appdev.eateryblueandroid.ui.components.home.Main

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    selectEatery: (eatery: Eatery) -> Unit,
    selectSection: (eaterySection: EaterySection) -> Unit,
    scrollState: LazyListState
) {
    Column {
        TopBar(
            label = "Eatery",
            expanded = scrollState.firstVisibleItemIndex == 0,
            eateryIcon = true,
            rightIcon = painterResource(id = R.drawable.ic_search)
        )
        val state = homeViewModel.state.collectAsState()
        state.value.let {
            when (it) {
                is HomeViewModel.State.Loading ->
                    Text(text = "gucci")
                is HomeViewModel.State.Data -> {
                    // Makes a new list for sections that are NOT empty, passes that instead. Recomposes fine.
                    val newSections : MutableList<EaterySection> = ArrayList()
                    it.sections.forEach { section ->
                        if (it.eateries.any { eatery -> section.filter(eatery) }) { newSections.add(section) }
                    }
                    Main(
                        scrollState = scrollState,
                        eateries = it.eateries,
                        sections = newSections,
                        selectEatery = selectEatery,
                        selectSection = selectSection
                    )
                }
                is HomeViewModel.State.Failure ->
                    Text("FAILURE")
            }
        }
    }
}