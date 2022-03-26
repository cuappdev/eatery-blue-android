package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.home.MainItem
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel

@Composable
fun NoWordsTypedSearchScreen(

    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit,
    searchViewModel: SearchViewModel,
    homeViewModel: HomeViewModel,
    selectSection: (eaterySection: EaterySection) -> Unit,
) {
//    var filterEatery = mutableListOf<Eatery>()
//    eateries.forEach{
//            eatery ->
//        if(eatery.isFavorite()){
//            filterEatery.add(eatery)
//        }
//    }

    val searchItem: List<SearchItem> = listOf(
        listOf(SearchItem.SearchBox),
        listOf(SearchItem.FavoriteLabel),
        listOf(SearchItem.Favorite),
        listOf(SearchItem.RecentSearchLabel),
        listOf(SearchItem.RecentlySearched),

    ).flatten()

    LazyColumn(
        contentPadding = PaddingValues(bottom=30.dp)
    ) {
                items(searchItem) { item ->
            when (item) {
                is SearchItem.SearchBox ->
                    TypeableSearchBar(searchViewModel,searchText = "")
                is SearchItem.FavoriteLabel ->
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Favorites",
                            textStyle = TextStyle.HEADER_H3
                        )

                            CircularBackgroundIcon(
                                icon = painterResource(
                                    id = R.drawable.ic_rightarrow
                                ),
                                onTap = {selectSection(EaterySection("Favorite Eateries") { it.isFavorite() })},
                                clickable = true,
                            )

                    }
                is SearchItem.Favorite ->
                    SearchFavoriteList(eateries = eateries, selectEatery = selectEatery)
                is SearchItem.RecentSearchLabel ->
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Searches",
                            textStyle = TextStyle.HEADER_H3
                        )
                    }
                is SearchItem.RecentlySearched ->
                    RecentSearchList(selectEatery = selectEatery)

            }
        }
//        SearchFavoriteList(eateries, selectEatery)
    }


}

sealed class SearchItem {
    object SearchBox: SearchItem()
    object RecentlySearched: SearchItem()
    object Favorite: SearchItem()
    object FavoriteLabel:  SearchItem()
    object RecentSearchLabel:  SearchItem()
}

//
//@Preview
//@Composable
//fun ComposablePreview() {
//    NoWordsTypedSearchScreen()
//}