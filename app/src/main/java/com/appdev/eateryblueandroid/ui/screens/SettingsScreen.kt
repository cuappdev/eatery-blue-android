package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.User
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun SettingsScreen(profileViewModel: ProfileViewModel) {
    val state = profileViewModel.state.collectAsState()
    val onBack = {
        if (state.value is ProfileViewModel.State.ProfileData) {
            profileViewModel.transitionProfile()
        } else {
            profileViewModel.transitionLogin()
        }
    }
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
                    .clickable { onBack() }
            )
        }
        Text(
            text = "Settings",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)
        )
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_appdev),
            title = "About Eatery",
            description = "Learn more about Cornell AppDev",
            onClick = { profileViewModel.transitionAbout() }
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_star_outline),
            title = "Favorites",
            description = "Manage your favorite eateries and items",
            onClick = { profileViewModel.transitionFavorites() }
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_bell),
            title = "Notifications",
            description = "Manage item and promotional notifications",
            onClick = { profileViewModel.transitionNotifications() }
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_lock),
            title = "Privacy",
            description = "Manage permissions and analytics",
            onClick = { profileViewModel.transitionPrivacy() }
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_gavel),
            title = "Legal",
            description = "Find terms, conditions, and privacy policy",
            onClick = { profileViewModel.transitionLegal() }
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_question_circ),
            title = "Support",
            description = "Report issues and contact Cornell Appdev",
            onClick = { profileViewModel.transitionSupport() }
        )
        state.value.let {
            if (it is ProfileViewModel.State.ProfileData) {
                LogoutSection(
                    profileViewModel::logout,
                    (state.value as ProfileViewModel.State.ProfileData).user.userName!!
                )
            }
        }
    }

    BackHandler {
        onBack()
    }
}

@Composable
fun SettingsOption(icon: Painter?, title: String, description: String, onClick: () -> Unit) {
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
            modifier = Modifier.padding(start = (if (icon != null) 5 else 0).dp)
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
                    modifier = if (description.isNotEmpty()) Modifier.padding(top = 12.dp) else Modifier.padding(top = 16.dp, bottom = 16.dp))
                if (description.isNotEmpty())
                    Text(
                        text = description,
                        textStyle = TextStyle.LABEL_SEMIBOLD,
                        color = colorResource(id = R.color.gray05),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colorResource(id = R.color.eateryBlue),
            modifier = Modifier.padding(end = 5.dp),
        )
    }
}

@Composable
fun SettingsLineSeparator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colorResource(id = R.color.gray01))
    ) {
    }
}

@Composable
fun LogoutSection(logout: () -> Unit, netId: String) {
    var id = netId
    if (id.contains("@")) id = netId.substring(0, netId.indexOf("@"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Logged in as " + id,
            textStyle = TextStyle.HEADER_H4,
            color = colorResource(id = R.color.gray05)
        )
        Surface(
            shape = RoundedCornerShape(25.dp),
        ) {
            Row(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.gray00))
                    .padding(top = 8.dp, bottom = 8.dp, start = 14.dp, end = 14.dp)
                    .clickable { logout() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = null,
                    tint = colorResource(id = R.color.black),
                    modifier = Modifier.padding(top = 1.dp)
                )
                Text(
                    text = "Log out",
                    textStyle = TextStyle.BODY_SEMIBOLD,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

        }

    }
}