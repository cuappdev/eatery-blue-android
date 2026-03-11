package com.cornellappdev.android.eatery.ui.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero

@Composable
fun FavoriteItemRow(
    itemName: String,
    eateries: List<String>,
    newNotif: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (newNotif) painterResource(id = R.drawable.ic_new_notif_star)
            else painterResource(id = R.drawable.ic_notif_star),
            contentDescription = "Notification Star Icon",
            tint = Color.Unspecified,
            modifier = Modifier.padding(end = 12.dp)
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
                    text = "Today",
                    fontSize = 10.sp,
                    style = EateryBlueTypography.body1,
                    color = Color.DarkGray
                )
            }
            Row {
                CondenseEateriesName(eateries)
            }
        }
        IconButton(
            onClick = onClick,
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


/**
 * Displays a formatted Text composable summarizing a list of eatery names based on the number of eateries.
 * Also prepends "At" to the eateries list.
 * - If there is only one eatery, it displays the condensed name of that eatery.
 * - If there are two eateries, it displays the condensed eatery name of the last eatery
 *      followed by "and 1 other eatery."
 * - If there are three or more eateries, it displays the condensed eatery name of the last eatery
 *      followed by "and n other eateries," where n is the count of additional eateries.
 *
 * @param eateries A list of eatery names to display.
 * @Composable
 * @return A Text composable that shows the primary eatery name with a summary of additional eateries,
 *      if any.
 */
@Composable
private fun CondenseEateriesName(eateries: List<String>) {
    val condensed = eateries.map { condenseDiningHallNames(it) }
    val text = if (eateries.size > 1) {
        "${condensed.last()} + ${eateries.size - 1} other"
    } else {
        condensed.lastOrNull() ?: ""
    }

    val suffix = when (condensed.size) {
        1 -> {
            ""
        }

        2 -> {
            "eatery"
        }

        else -> {
            "eateries"
        }
    }

    Text(
        text = "At ",
        fontSize = 12.sp
    )
    Text(
        text = text,
        fontWeight = FontWeight(600),
        fontSize = 12.sp
    )
    Text(
        text = " $suffix",
        fontSize = 12.sp
    )
}

/**
 * Condenses the specified dining hall name by removing the phrase "Dining Room."
 * If the name contains "Dining Room," the function returns the name without this phrase.
 * Special Case: If the name is "Jansen's Dining Room at Bethe House," it returns "Bethe House."
 *
 * @param name The original dining hall name to be condensed.
 * @return The condensed dining hall name with "Dining Room" removed,
 *         or "Bethe House" for the specific "Jansen's Dining Room at Bethe House."
 */
private fun condenseDiningHallNames(name: String): String {
    if (name.contains("Bethe", ignoreCase = true)) return "Bethe House"
    if (name.contains("Dining Room", ignoreCase = true)) {
        return name.replace("Dining Room", "", ignoreCase = true).trim()
    }
    return name
}