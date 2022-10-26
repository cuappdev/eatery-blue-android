package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainLoading(scrollState: LazyListState) {
    var mainItems: List<MainLoadingItem> by remember { mutableStateOf(listOf()) }
    mainItems = listOf(
        listOf(MainLoadingItem.SearchBox),
        listOf(MainLoadingItem.FilterOptions),
        listOf(MainLoadingItem.EaterySectionLabel(stringResource(R.string.home_loading_0))),
        listOf(MainLoadingItem.EaterySectionList),
        listOf(MainLoadingItem.EaterySectionLabel(stringResource(R.string.home_loading_1))),
        listOf(MainLoadingItem.EaterySectionList),
        listOf(MainLoadingItem.Spacer),
        List(15) { MainLoadingItem.EateryItem }
    ).flatten()

    @Composable
    fun FilterItem(width: Dp, modifier: Modifier = Modifier) {
        Surface(
            modifier = Modifier
                .padding(end = 8.dp)
                .then(modifier)
                .clip(
                    RoundedCornerShape(
                        CornerSize(100.dp),
                    )
                )
                .width(width)
                .fillMaxHeight(),
            color = colorResource(R.color.gray00)
        ) {}
    }

    @Composable
    fun EateryBlob(
        modifier: Modifier = Modifier,
        fillMaxWidth: Boolean = false,
        height: Dp = 186.dp
    ) {
        Surface(
            modifier = Modifier
                .padding(end = 12.dp)
                .then(modifier)
                .clip(
                    RoundedCornerShape(
                        CornerSize(8.dp),
                    )
                ).then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(295.dp))
                .height(height),
            color = colorResource(R.color.gray00)
        ) {}
    }


    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        items(mainItems) { item ->
            when (item) {
                is MainLoadingItem.SearchBox -> {
                    Surface(
                        modifier = Modifier
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                            .height(38.dp)
                            .fillMaxWidth(),
                        color = colorResource(R.color.gray00),
                        shape = RoundedCornerShape(
                            CornerSize(8.dp)
                        )
                    ) {}
                }
                is MainLoadingItem.FilterOptions -> {
                    Row(
                        modifier = Modifier
                            .height(34.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        FilterItem(110.dp, modifier = Modifier.padding(start = 16.dp))
                        FilterItem(160.dp)
                        FilterItem(80.dp)
                        FilterItem(60.dp)
                        FilterItem(100.dp)
                        FilterItem(120.dp, modifier = Modifier.padding(end = 8.dp))

                    }
                }
                is MainLoadingItem.EaterySectionLabel -> {
                    Text(
                        text = item.label,
                        textStyle = TextStyle.HEADER_H3,
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 16.dp),
                        color = colorResource(R.color.gray02)
                    )
                }
                is MainLoadingItem.EaterySectionList -> {
                    CompositionLocalProvider(
                        LocalOverscrollConfiguration provides null
                    ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(bottom = 12.dp)
                        ) {
                            EateryBlob(modifier = Modifier.padding(start = 16.dp))
                            EateryBlob()
                            EateryBlob()
                            EateryBlob()
                        }
                    }
                }
                is MainLoadingItem.Spacer -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Loading Eateries...",
                        textStyle = TextStyle.HEADER_H3,
                        modifier = Modifier.padding(start = 16.dp),
                        color = colorResource(R.color.gray02)
                    )
                }
                is MainLoadingItem.EateryItem -> {
                    EateryBlob(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                        height = 216.dp,
                        fillMaxWidth = true
                    )
                }
            }
        }

    }
}

private sealed class MainLoadingItem {
    object SearchBox : MainLoadingItem()
    object FilterOptions : MainLoadingItem()
    data class EaterySectionLabel(
        val label: String
    ) : MainLoadingItem()
    object Spacer : MainLoadingItem()
    object EaterySectionList : MainLoadingItem()
    object EateryItem : MainLoadingItem()
}