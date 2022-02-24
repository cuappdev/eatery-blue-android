package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
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
    Column(modifier = Modifier
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
                modifier = Modifier.clickable {
                    onBack()
                }
            )
        }
        Text(
            text = "Settings",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp)
        )
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_appdev),
            title = "About Eatery",
            description = "Learn more about Cornell AppDev",
            onClick = {}
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_star_outline),
            title = "Favorites",
            description = "Manage your favorite eateries and items",
            onClick = {}
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_bell),
            title = "Notifications",
            description = "Manage item and promotional notifications",
            onClick = {}
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_lock),
            title = "Privacy",
            description = "Manage permissions and analytics",
            onClick = {}
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_gavel),
            title = "Legal",
            description = "Find terms, conditions, and privacy policy",
            onClick = {}
        )
        SettingsLineSeparator()
        SettingsOption(
            icon = painterResource(id = R.drawable.ic_question_circ),
            title = "Support",
            description = "Report issues and contact Cornell Appdev",
            onClick = {}
        )
        state.value.let {
            if (it is ProfileViewModel.State.ProfileData) {
                LogoutSection(profileViewModel::logout)
            }
        }
    }

    BackHandler {
        onBack()
    }
}

@Composable
fun SettingsOption(icon: Painter, title: String, description: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 11.dp, bottom = 11.dp)
            .clickable {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = colorResource(id = R.color.gray05),
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(title, textStyle = TextStyle.HEADER_H4)
                Text(
                    text = description,
                    textStyle = TextStyle.LABEL_SEMIBOLD,
                    color = colorResource(id = R.color.gray05),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colorResource(id = R.color.eateryBlue),
        )
    }
}

@Composable
fun SettingsLineSeparator() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(colorResource(id = R.color.gray01))) {
    }
}

@Composable
fun LogoutSection(logout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Logged in as cyr7",
            textStyle = TextStyle.HEADER_H4,
            color = colorResource(id = R.color.gray05)
        )
        Surface(
            shape = RoundedCornerShape(25.dp),
        ) {
            Row(
                modifier = Modifier.background(color = colorResource(id = R.color.gray00))
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