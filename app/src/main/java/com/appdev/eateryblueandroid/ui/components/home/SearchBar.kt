package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun SearchBar(selectSearch: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.clickable {selectSearch() }.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.gray00))
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_magnifying_glass),
                tint = colorResource(id = R.color.gray05),
                contentDescription = null,
                modifier = Modifier.padding(top = 3.dp)
            )
            Text(
                text = "Search for grub...", 
                textStyle = TextStyle.BODY_MEDIUM,
                color = colorResource(id = R.color.gray05),
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
}
