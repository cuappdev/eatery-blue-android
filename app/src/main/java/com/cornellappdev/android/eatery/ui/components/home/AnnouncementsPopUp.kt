package com.cornellappdev.android.eatery.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.viewmodels.HomeViewModel

@Composable
fun AnnouncementsPopUp(
    homeViewModel: HomeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .background(Color.Transparent, RoundedCornerShape(20.dp))
    ) {
        if (!homeViewModel.bigPopUp) {
            Popup(alignment = Alignment.BottomEnd) {

                Box(
                    Modifier
                        .padding(16.dp)
                        .width(50.dp)
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { homeViewModel.setPopUp(true) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_appdev),
                        tint = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(.6f),
                        contentDescription = "popup logo"
                    )
                }
            }
        } else {
            Popup(alignment = Alignment.Center, properties = PopupProperties(focusable = true)) {
                Box(
                    Modifier
                        .fillMaxWidth(.8f)
                        .fillMaxHeight(.4f)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .focusable(true)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.recruitment_popup_2),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentDescription = null
                    )
                    IconButton(
                        onClick = {
                            homeViewModel.setPopUp(false)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color.Transparent, shape = CircleShape)
                            .align(Alignment.TopEnd)
                            .alpha(.4f)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = Icons.Default.Close.name,
                            Modifier
                                .size(30.dp)
                                .background(Color.White, CircleShape)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }
    }
}
