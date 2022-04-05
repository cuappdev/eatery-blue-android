package com.appdev.eateryblueandroid.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun SettingsOption(
    icon: Painter? = null,
    title: String,
    description: String = "",
    onClick: () -> Unit,
    pointerIcon: Painter? = painterResource(
        id = R.drawable.ic_chevron_right
    )
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick() },
                interactionSource = interactionSource,
                indication = rememberRipple()
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = (if (icon != null) 5 else 0).dp).fillMaxWidth(.9f)
        ) {
            if (icon != null)
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray05),
                )
            Column(modifier = Modifier.padding(start = (if (icon != null) 10 else 0).dp)) {
                Text(
                    title,
                    textStyle = TextStyle.HEADER_H4,
                    modifier = if (description.isNotEmpty()) Modifier.padding(top = 12.dp) else Modifier.padding(
                        top = 16.dp,
                        bottom = 16.dp
                    )
                )
                if (description.isNotEmpty())
                    Text(
                        text = description,
                        textStyle = TextStyle.LABEL_SEMIBOLD,
                        color = colorResource(id = R.color.gray05),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
            }
        }
        if (pointerIcon != null)
            Icon(
                painter = pointerIcon,
                contentDescription = null,
                tint = colorResource(id = R.color.eateryBlue),
                modifier = Modifier.padding(start = 16.dp, end = 5.dp),
            )
    }
}