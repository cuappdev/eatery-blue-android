package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.viewmodels.EateryListViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.max
import com.appdev.eateryblueandroid.models.Eatery
import java.lang.Float.min

@Composable

fun EateryListScreen(
    eateryListViewModel: EateryListViewModel,
    selectEatery: (eatery: Eatery) -> Unit,
    scrollState: LazyListState
){
    val scrollOffset: Float = min(
        1f,
        1 - (scrollState.firstVisibleItemScrollOffset / 600f + scrollState.firstVisibleItemIndex)
    )
    val heightSize by animateDpAsState(targetValue = max(50.dp, 100.dp * scrollOffset))
    //val dynamicLines = max(3f, scrollOffset * 6).toInt()
    Scaffold(
//        topBar = {
//            TopAppBar(
//                backgroundColor = colorResource(R.color.eateryBlue),
//                modifier = Modifier
//                    .height(heightSize)
//            ) {
//                Column(
//                ) {
//                    Image(painter = painterResource(id = R.drawable.ic_eaterylogo), contentDescription = null)
//                    Text(text = "Eatery", color = colorResource(id = R.color.white), fontSize = 34.sp)
//                }
//            }
//        }
    ) {
        val state = eateryListViewModel.state.collectAsState()
        state.value.let {
            when(it) {
                is EateryListViewModel.State.Loading ->
                    Text(text = "gucci")
                is EateryListViewModel.State.Data ->
                    LazyColumn(
                        state = scrollState,
                        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp)
                    ) {
                        items(it.data) { eatery ->
                            Column(
                                modifier = Modifier
                                    .padding(0.dp, 5.dp)
                                    .clickable { selectEatery(eatery) }
                            ) {
                                EateryCard(eatery = eatery)
                            }
                        }
                    }
                is EateryListViewModel.State.Failure ->
                    Text("FAILURE")
            }
        }
    }
}