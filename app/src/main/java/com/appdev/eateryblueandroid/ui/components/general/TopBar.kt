package com.appdev.eateryblueandroid.ui.components.general

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TopBar(
    label: String,
    expanded: Boolean,
    eateryIcon: Boolean,
    rightIcon: Painter?
) {
    val transition = updateTransition(expanded, label = "TopBarAnimation")
    val expandedAlpha by transition.animateFloat(label = "ExpandedAlpha") { state ->
        when(state) {
            true -> 1f
            false -> 0f
        }
    }
    val minimizedAlpha by transition.animateFloat(label = "MinimizedAlpha") { state ->
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
        Box(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 7.dp)
                    .alpha(expandedAlpha),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eaterylogo),
                    contentDescription = null,
                    tint = colorResource(id = R.color.white),
                    modifier = Modifier.alpha(if(eateryIcon) 1f else 0f)
                )
                Text(
                    text = label,
                    color = colorResource(id = R.color.white),
                    textStyle = TextStyle.HEADER_H1
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 7.dp)
                    .alpha(minimizedAlpha),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    color = colorResource(id = R.color.white),
                    textStyle = TextStyle.SUBTITLE
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 7.dp)
                    .alpha(minimizedAlpha),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                if (rightIcon != null) Icon(
                    painter = rightIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorResource(id = R.color.white)
                )
            }
        }
    }
}