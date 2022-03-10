package com.appdev.eateryblueandroid.ui.components.core

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R


@Composable
fun CircularBackgroundIcon(
    icon: Painter,
    clickable: Boolean,
    iconTint: Color = colorResource(id = R.color.black),
    iconWidth: Dp = 20.dp,
    iconHeight: Dp = 20.dp,
    iconDescription: String? = null,
    backgroundTint: Color = colorResource(id = R.color.gray00),
    backgroundSize: Dp = 40.dp,
    onTap: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(backgroundSize/2)
    ) {
        Row(
            modifier = Modifier
                .background(backgroundTint)
                .width(backgroundSize)
                .height(backgroundSize)
                .then(if(clickable) Modifier.clickable { onTap() } else Modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = iconDescription,
                tint = iconTint,
                modifier = Modifier
                    .width(iconWidth)
                    .height(iconHeight)

            )
        }
    }
}
