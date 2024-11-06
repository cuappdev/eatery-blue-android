package com.cornellappdev.android.eatery.ui.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero

@Composable
fun FavoriteItemRow(
    itemName: String,
    atEateries: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.notif_star),
            contentDescription = "Notification Star Icon",
            tint = Color.Yellow
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemName,
                    style = EateryBlueTypography.h5,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = "today",
                    fontSize = 10.sp,
                    style = EateryBlueTypography.body1,
                    color = Color.LightGray
                )
            }
            Row {
                condenseEateriesName(atEateries)
            }
        }
        IconButton(
            onClick = {
                // TODO: Add click action here
            },
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = GrayZero,
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun condenseEateriesName(atEateries: List<String>) {
    val text = if (atEateries.size > 1) {
        "${atEateries.last()} + ${atEateries.size - 1} other"
    } else {
        atEateries.lastOrNull() ?: ""
    }

    val suffix = when(atEateries.size){
        1 -> {
            ""
        }
        2->{
            "eatery"
        }
        else-> {
            "eateries"
        }
    }

    Text(
        text = "At $text $suffix"
    )
}
