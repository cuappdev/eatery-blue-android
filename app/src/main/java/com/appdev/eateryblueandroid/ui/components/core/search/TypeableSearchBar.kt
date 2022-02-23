package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TypeableSearchBar(searchText : String) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.clickable {}.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                modifier = Modifier
                    .background(colorResource(id = R.color.gray00))
                    .padding(10.dp)
                    .width(250.dp)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_magnifying_glass),
                    tint = colorResource(id = R.color.gray05),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Text(
                    text = searchText,
                    textStyle = TextStyle.BODY_MEDIUM,
                    color = colorResource(id = R.color.gray05),
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Text(
                text = "Cancel",
                textStyle = TextStyle.BODY_MEDIUM,
                color = colorResource(id = R.color.black),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.CenterVertically)
            )
        }

    }
}

@Preview
@Composable
fun PreviewTypeableSearchBar(){
    TypeableSearchBar("hello")
}
