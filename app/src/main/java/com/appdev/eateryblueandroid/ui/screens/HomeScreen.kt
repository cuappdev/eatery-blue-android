package com.appdev.eateryblueandroid.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
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
import com.appdev.eateryblueandroid.ui.viewmodels.AllEateriesViewModel
import androidx.compose.foundation.Image

@Composable
fun SafeHomeScreen(allEateriesViewModel: AllEateriesViewModel){
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colorResource(R.color.eateryBlue),
                modifier = Modifier.fillMaxHeight(0.13f)) {
                Column(
                ) {
                    Image(painter = painterResource(id = R.drawable.ic_eaterylogo), contentDescription = null)
                    Text(text = "Eatery", color = colorResource(id = R.color.white), fontSize = 34.sp)
                }
            }
        }
    ) {
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
}