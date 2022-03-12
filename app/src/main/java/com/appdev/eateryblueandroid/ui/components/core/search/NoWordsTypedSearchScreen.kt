package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R

@Composable

fun NoWordsTypedSearchScreen() {

    Icon(
        painter = painterResource(id = R.drawable.ic_star_outline),
        tint = colorResource(id = R.color.gray05),
        modifier = Modifier.padding(top = 3.dp),
        contentDescription = null
    )

}

@Preview
@Composable
fun ComposablePreview() {
    NoWordsTypedSearchScreen()
}
