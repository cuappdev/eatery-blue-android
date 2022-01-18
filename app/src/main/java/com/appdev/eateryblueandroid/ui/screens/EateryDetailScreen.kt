package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.EateryViewModel

@Composable
fun EateryDetailScreen(eateryViewModel: EateryViewModel) {
    val state = eateryViewModel.state.collectAsState()
    state.value.let {
        when (it) {
            is EateryViewModel.State.Loading ->
                Text("Loading")
            is EateryViewModel.State.Data ->
                Text(it.data.name ?: "No name")
        }
    }
}