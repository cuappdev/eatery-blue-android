package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.google.firebase.messaging.RemoteMessage.Notification

@Composable
fun NotificationsScreen(

){
    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Notifications",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )
    }
}
