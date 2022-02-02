package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TopBar(scrollState: LazyListState) {
    val expanded = scrollState.firstVisibleItemIndex == 0
    val transition = updateTransition(expanded, label = "TopBarAnimation")
    val primaryAlpha by transition.animateFloat(label = "TopBarPrimaryAlpha") { state ->
        when(state) {
            true -> 1f
            false -> 0f
        }
    }
    val secondaryAlpha by transition.animateFloat(label = "TopBarSecondaryAlpha") { state ->
        when(state) {
            true -> 0f
            false -> 1f
        }
    }
    val height by transition.animateDp(label = "TopBarHeight") { state ->
        when(state) {
            true -> 100.dp
            false -> 60.dp
        }
    }
    TopAppBar(
        backgroundColor = colorResource(R.color.eateryBlue),
        modifier = Modifier.height(height)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column (modifier = Modifier.weight(1f).alpha(primaryAlpha)){
                Icon(
                    painter = painterResource(id = R.drawable.ic_eaterylogo),
                    contentDescription = null,
                    tint = colorResource(id = R.color.white)
                )
                Text(
                    text = "Eatery",
                    color = colorResource(id = R.color.white),
                    textStyle = TextStyle.HEADER_H1
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 7.dp)
                    .alpha(secondaryAlpha),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Eatery",
                    color = colorResource(id = R.color.white),
                    textStyle = TextStyle.SUBTITLE
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 7.dp)
                    .alpha(secondaryAlpha),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorResource(id = R.color.white)
                )
            }
        }
    }
}