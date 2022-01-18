package com.appdev.eateryblueandroid.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.viewmodels.AllEateriesViewModel

@Composable
fun SafeHomeScreen(allEateriesViewModel: AllEateriesViewModel){
    val state = allEateriesViewModel.state.collectAsState()
    state.value.let {
        when(it) {
            is AllEateriesViewModel.State.Loading ->
                Text(text = "gucci")
            is AllEateriesViewModel.State.Data ->
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp)
                ) {
                    items(it.data) { eatery ->
                        Column(
                            modifier = Modifier.padding(0.dp, 5.dp)
                        ) {
                            EateryCard(eatery = eatery)
                        }
                    }
                }
            is AllEateriesViewModel.State.Failure ->
                Text("FAILURE")
        }
    }
}