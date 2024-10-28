package com.cornellappdev.android.eatery.ui.components.upcoming

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.FavoriteIcon
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero


data class MenuCardViewState(
    val eateryId: Int,
    val menu: List<MenuCategoryViewState>,
    val name: String,
    val eateryHours: EateryHours?,
    val eateryStatus: EateryStatus?,
)

data class EateryStatus(
    val statusText: String,
    val statusColor: Color,
)

data class EateryHours(
    val startTime: String,
    val endTime: String,
)

/**
 * Represents the card for each eatery that is shown in the list
 * in the upcoming menu screen
 */
@Composable
fun MenuCard(
    menuCardViewState: MenuCardViewState,
    selectEatery: (eateryId: Int) -> Unit = {},
) = with(menuCardViewState) {
    var openDropdown by remember { mutableStateOf(false) }
    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White,
        modifier = Modifier.clickable {
            openDropdown = !openDropdown
        }
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 5.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = menuCardViewState.name,
                    style = EateryBlueTypography.h5,
                )
                Icon(
                    imageVector = if (!openDropdown) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                    contentDescription = "Expand menu",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(top = 10.dp, end = 20.dp)
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                )

                Row(modifier = Modifier.padding(top = 24.dp)) {
                    eateryStatus?.let {
                        Text(
                            text = it.statusText,
                            style = EateryBlueTypography.subtitle2,
                            color = it.statusColor
                        )
                        Text(
                            text = " • ",
                            style = EateryBlueTypography.subtitle2,
                            color = GrayFive
                        )
                    }
                    eateryHours?.let {
                        Text(
                            modifier = Modifier.padding(
                                bottom = 8.dp
                            ),
                            text = "${it.startTime} - ${it.endTime}",
                            style = EateryBlueTypography.subtitle2,
                            color = GrayFive
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .animateContentSize(tween(250))
                    .fillMaxWidth()
            ) {
                if (openDropdown) {
                    Spacer(
                        modifier = Modifier
                            .padding(end = 12.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayZero, CircleShape)
                    )
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        EateryDetails(
                            selectEatery = { selectEatery(eateryId) },
                            menu = menu,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EateryDetails(
    selectEatery: () -> Unit,
    menu: List<MenuCategoryViewState>
) {
    Column {
        EateryEventMenu(menu)
        Card(
            shape = RoundedCornerShape(20.dp),
            onClick = {
                selectEatery()
            },
            backgroundColor = GrayZero,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {

            Row(
                modifier = Modifier.padding(
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_eatery),
                    contentDescription = null,
                    tint = Color.Black
                )
                Text(
                    text = "View Eatery Details",
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}

@Composable
private fun EateryEventMenu(menu: List<MenuCategoryViewState>) {
    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            menu.forEach { category ->
                Text(
                    text = category.category,
                    style = EateryBlueTypography.h5,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                )
                category.items.forEach { item ->
                    MenuItemDisplay(item = item)
                }
            }
        }
    }
}

@Composable
private fun MenuItemDisplay(item: MenuItemViewState) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = item.item.name ?: "Unknown",
            style = EateryBlueTypography.caption,
            fontWeight = FontWeight.Normal
        )
        FavoriteIcon(item.isFavorite)
    }
}
