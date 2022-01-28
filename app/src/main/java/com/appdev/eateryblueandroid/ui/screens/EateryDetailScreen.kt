package com.appdev.eateryblueandroid.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel

@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel,
    hideEatery: () -> Unit
) {
    val state = eateryDetailViewModel.state.collectAsState()
    state.value.let {
        when (it) {
            is EateryDetailViewModel.State.Empty ->
                Text("Error")
            is EateryDetailViewModel.State.Data ->
                Column(
                    modifier = Modifier.padding(90.dp)
                ) {
                    Text(
                        text = it.data.name ?: "No name",
                        fontSize=20.sp
                    )
                }
        }
    }
    
    BackHandler {
        hideEatery()
    }
}