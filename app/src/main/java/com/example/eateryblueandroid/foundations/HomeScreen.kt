package com.example.eateryblueandroid.foundations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.eateryblueandroid.R


@Composable
fun HomeScreen(){
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

    }
}