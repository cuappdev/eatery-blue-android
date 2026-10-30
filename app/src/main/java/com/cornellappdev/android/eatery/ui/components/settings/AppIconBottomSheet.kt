package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.AppIcon
import com.cornellappdev.android.eatery.util.changeIcon
import com.cornellappdev.android.eatery.util.currentIcon
import com.cornellappdev.android.eatery.util.iconMap

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconBottomSheet(hide: () -> Unit) {
    val context = LocalContext.current
    val (selectedAppIcon, setSelectedAppIcon) = remember { mutableStateOf(currentIcon(context)) }
    val currentIcon = currentIcon(context)
    Column(
        modifier = Modifier
            .background(currentColors.backgroundDefault)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_app_icon_title),
                style = EateryBlueTypography.h4,
                color = currentColors.textPrimary,
            )

            IconButton(
                onClick = {
                    hide()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = currentColors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = currentColors.textPrimary)
            }
        }
        val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 2 })
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.offset(x = (-8).dp)
        ) {
            Column {
                when (it) {
                    0 -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppIconButton(AppIcon.DEFAULT, selectedAppIcon, setSelectedAppIcon)
                            AppIconButton(AppIcon.BLUE, selectedAppIcon, setSelectedAppIcon)
                            AppIconButton(AppIcon.RED, selectedAppIcon, setSelectedAppIcon)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppIconButton(AppIcon.GREEN, selectedAppIcon, setSelectedAppIcon)
                            AppIconButton(AppIcon.YELLOW, selectedAppIcon, setSelectedAppIcon)
                            AppIconButton(AppIcon.ORANGE, selectedAppIcon, setSelectedAppIcon)
                        }
                    }

                    1 -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppIconButton(AppIcon.VALENTINES, selectedAppIcon, setSelectedAppIcon)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                        }
                    }
                }
            }
        }


        Button(
            onClick =
                {
                    changeIcon(context, selectedAppIcon)
                    hide()
                },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = currentColors.accentPrimary,
                contentColor = currentColors.backgroundDefault
            )
        ) {
            Text(
                text = stringResource(R.string.done),
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                color = currentColors.textPrimary
            )
        }

        TextButton(
            enabled = selectedAppIcon != currentIcon,
            onClick = {
                setSelectedAppIcon(currentIcon)
                hide()
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.reset),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = if (selectedAppIcon != currentIcon) currentColors.textPrimary else currentColors.backgroundSecondary
            )
        }
    }
}

@Composable
fun AppIconButton(
    appIcon: AppIcon,
    selectedAppIcon: AppIcon,
    setSelectedAppIcon: (AppIcon) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .padding(start = 18.dp, bottom = 12.dp)
            .border(1.dp, currentColors.backgroundSecondary, shape = RoundedCornerShape(25.dp))
            .shadow(1.dp, RoundedCornerShape(25.dp), clip = true)
    ) {
        Box {
            Image(
                painter = painterResource(id = iconMap[appIcon]!!.second),
                contentDescription = null,
                alpha = if (appIcon == selectedAppIcon) 0.38f else 1f,
                modifier = Modifier
                    .size(72.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple()
                    ) {
                        setSelectedAppIcon(appIcon)
                    }
            )
            if (appIcon == selectedAppIcon) {
                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = currentColors.backgroundSecondary
                )
            }
        }
    }
}
