package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.settings.SettingsOption
import com.appdev.eateryblueandroid.ui.screens.SettingsLineSeparator
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun LegalScreen(profileViewModel: ProfileViewModel) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    val interactionSource = MutableInteractionSource()
    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_leftarrow),
                contentDescription = null,
                tint = colorResource(id = R.color.black),
                modifier = Modifier
                    .clickable(
                        onClick = { onBack() },
                        interactionSource = interactionSource,
                        indication = null
                    )
                    .clip(CircleShape)

            )
        }

        Text(
            text = "Legal",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Find terms, conditions, and privacy policy",
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SettingsOption(
            title = "Terms and Conditions",
            onClick = {},
            pointerIcon = painterResource(id = R.drawable.ic_upright_transfer_arrow)
        )
        SettingsLineSeparator()
        SettingsOption(
            title = "Privacy Policy",
            onClick = {},
            pointerIcon = painterResource(id = R.drawable.ic_upright_transfer_arrow)
        )
    }
    BackHandler {
        onBack()
    }
}


@Composable
fun LegalOption(title: String, onClick: () -> Unit) {
    val interactionSource = MutableInteractionSource()
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
        ) {
            Text(
                text = title,
                textStyle = TextStyle.HEADER_H4,
                color = colorResource(id = R.color.black),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_upright_transfer_arrow),
            contentDescription = null,
            tint = colorResource(id = R.color.eateryBlue),
            modifier = Modifier.padding(end = 4.6.dp)
        )
    }
}