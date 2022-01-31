package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TopBar() {
//    val scrollOffset: Float = java.lang.Float.min(
//        1f,
//        1 - (scrollState.firstVisibleItemScrollOffset / 600f + scrollState.firstVisibleItemIndex)
//    )
//    val heightSize by animateDpAsState(targetValue = max(50.dp, 100.dp * scrollOffset))
    //val dynamicLines = max(3f, scrollOffset * 6).toInt()
    //TopAppBar(
//                backgroundColor = colorResource(R.color.eateryBlue),
//                modifier = Modifier
//                    .height(heightSize)
//            ) {
//                Column(
//                ) {
//                    Image(painter = painterResource(id = R.drawable.ic_eaterylogo), contentDescription = null)
//                    Text(text = "Eatery", color = colorResource(id = R.color.white), fontSize = 34.sp)
//                }
//            }
    Text(text = "hi", textStyle = TextStyle.BODY_MEDIUM)
}