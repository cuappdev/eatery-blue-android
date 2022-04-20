package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.util.AppIcon
import com.appdev.eateryblueandroid.util.changeIcon
import com.appdev.eateryblueandroid.util.currentIcon

@Composable
fun AppIconSheet(
    appIconViewModel: BottomSheetViewModel
) {
    var selectedAppIconIndex by remember { mutableStateOf(currentIcon().ordinal) }
    val firstAppIndex = currentIcon().ordinal
    val setIndex = { num: Int -> selectedAppIconIndex = num }
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "App Icon",
                textStyle = TextStyle.HEADER_H3,
                color = colorResource(id = R.color.black),
            )
            CircularBackgroundIcon(
                icon = painterResource(id = R.drawable.ic_x),
                clickable = true,
                onTap = {
                    appIconViewModel.hide()
                },
                iconWidth = 12.dp,
                iconHeight = 12.dp,
                backgroundSize = 40.dp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 61.5.dp, end = 61.5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            AppIconButton(0, selectedAppIconIndex, setIndex)
            AppIconButton(1, selectedAppIconIndex, setIndex)
            AppIconButton(2, selectedAppIconIndex, setIndex)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AppIconButton(3, selectedAppIconIndex, setIndex)
            AppIconButton(4, selectedAppIconIndex, setIndex)
            AppIconButton(5, selectedAppIconIndex, setIndex)
        }

        Button(
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            onClick = {
                changeIcon(
                    when (selectedAppIconIndex) {
                        0 -> AppIcon.DEFAULT
                        1 -> AppIcon.BLUE
                        2 -> AppIcon.RED
                        3 -> AppIcon.GREEN
                        4 -> AppIcon.YELLOW
                        5 -> AppIcon.ORANGE
                        else -> AppIcon.DEFAULT
                    }
                )
                appIconViewModel.hide()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.eateryBlue),
                contentColor = colorResource(id = R.color.white)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Done",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.white)
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
        {
            Text(
                text = "Reset",
                textStyle = TextStyle.BODY_SEMIBOLD,
                color = if (firstAppIndex != selectedAppIconIndex) colorResource(id = R.color.black)
                else colorResource(id = R.color.gray01),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .then(
                        if (firstAppIndex != selectedAppIconIndex) Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            rememberRipple(bounded = false)
                        ) {
                            setIndex(firstAppIndex)
                        }
                        else Modifier
                    )

            )
        }


        Spacer(modifier = Modifier.padding(top = 70.dp))
    }
}

@Composable
fun AppIconButton(
    index: Int,
    selectedIndex: Int,
    setIndex: (Int) -> Unit
) {
    val interactionSource = MutableInteractionSource()
    val iconMapOff = hashMapOf(
        0 to R.drawable.ic_default_off,
        1 to R.drawable.ic_blue_off,
        2 to R.drawable.ic_red_off,
        3 to R.drawable.ic_green_off,
        4 to R.drawable.ic_yellow_off,
        5 to R.drawable.ic_orange_off
    )

    val iconMapOn = hashMapOf(
        0 to R.drawable.ic_default_on,
        1 to R.drawable.ic_blue_on,
        2 to R.drawable.ic_red_on,
        3 to R.drawable.ic_green_on,
        4 to R.drawable.ic_yellow_on,
        5 to R.drawable.ic_orange_on
    )
    Surface(
        modifier = Modifier
            .padding(start = 18.dp, bottom = 12.dp)
            .border(1.dp, colorResource(id = R.color.gray01), shape = RoundedCornerShape(25.dp))
            .shadow(1.dp, RoundedCornerShape(25.dp), clip = true)
    ) {
        Icon(
            painter = painterResource(id = (if (selectedIndex == index) iconMapOn else iconMapOff)[index]!!),
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                ) {
                    setIndex(index)
                },
            tint = Color.Unspecified
        )
    }
}