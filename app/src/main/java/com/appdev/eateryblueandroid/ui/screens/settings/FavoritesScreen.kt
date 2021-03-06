package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun FavoritesScreen(
    profileViewModel: ProfileViewModel,
    eateryState: State<HomeViewModel.State>,
    profileEateryDetailViewModel: EateryDetailViewModel
) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    val eateryDataList: MutableList<Any> = mutableListOf()
    if (eateryState.value is HomeViewModel.State.Data)
        (eateryState.value as HomeViewModel.State.Data).eateries.forEach {
            if (it.isFavorite()) eateryDataList.add(it)
        }
    eateryDataList.add(0, "header")


    val scrollState = rememberLazyListState()
    val interactionSource = MutableInteractionSource()
    if (eateryDataList.size > 1)
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = 50.dp, start = 16.dp, end = 16.dp)
        ) {
            items(eateryDataList) { item ->
                if (item is Eatery)
                    Column(
                        modifier = Modifier.padding(
                            bottom = 12.dp
                        )
                    ) {
                        EateryCard(eatery = item,
                            selectEatery =
                            fun(eatery: Eatery) {
                                profileEateryDetailViewModel.selectEatery(eatery)
                                profileViewModel.transitionEateryDetail()
                            }
                        )
                    }
                // Header
                else {
                    FavoritesHeader({ onBack() }, interactionSource)
                }
            }
        }
    else
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            FavoritesHeader({ onBack() }, interactionSource)
            Spacer(Modifier.padding(top = 120.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Text for when you have NO favorite eateries.
                // Must wait for design team to finalize empty screen designs... Keep blank for now!
                Text(
                    text = "",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.gray05),
                    modifier = Modifier
                )
            }
        }

    BackHandler {
        onBack()
    }
}

@Composable
private fun FavoritesHeader(
    onBack: () -> Unit,
    interactionSource: MutableInteractionSource
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 41.dp, bottom = 5.dp)
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
        text = "Favorites",
        color = colorResource(id = R.color.eateryBlue),
        textStyle = TextStyle.HEADER_H1,
        modifier = Modifier.padding(top = 7.dp)
    )
    Text(
        text = "Manage your favorite eateries",
        textStyle = TextStyle.APPDEV_BODY_MEDIUM,
        color = colorResource(id = R.color.gray06),
        modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
    )
}