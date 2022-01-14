package com.appdev.eateryblueandroid.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.foundations.searchBar

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
                    Text(text = "EateryBlue", color = colorResource(id = R.color.white), fontSize = 34.sp)
                }
            }
        }
    ) {
        Text("hi")
    }
}