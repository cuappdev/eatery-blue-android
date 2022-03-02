package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R

@Composable
fun SectionSeparator() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(id = R.color.gray01))
        ) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(colorResource(id = R.color.gray00))
        ) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(id = R.color.gray01))
        ) {}
    }
}