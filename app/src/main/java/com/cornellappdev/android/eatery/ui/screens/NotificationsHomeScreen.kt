package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.components.notifications.FavoriteItemRow
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography

@Composable
fun NotificationsHomeScreen(

){
    Column(
        modifier = Modifier
            .padding(top = 40.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Notifications",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp, bottom = 20.dp)
        )
        Text(
            text = "Favorite Items",
            style = EateryBlueTypography.h4,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyColumn {
            item{
                FavoriteItemRow(
                    "Chicken Nuggets", listOf("Rose House")
                )
            }
            item{
                FavoriteItemRow(
                    "French Fries", listOf("Bethe House")
                )
            }
        }



    }
}