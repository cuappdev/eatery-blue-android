package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun FavoritesToggle(
    onClick: () -> Unit,
    label: String,
    active: Boolean
) {
    val detailColor = if (active) EateryBlue else Color(0xFF6B6B6B)
    val backgroundColor = if (active) Color.White else GrayZero
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8))
            .clickable { onClick() }
            .border(BorderStroke(Dp.Hairline, detailColor), RoundedCornerShape(8))
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .size(width = 160.dp, height = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Center content within the Row
    ) {
        Text(
            text = label,
            color = detailColor,
            style = EateryBlueTypography.button
        )
    }

}

@Composable
fun ToggleRow(toggle: Boolean, setToggle: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = (Arrangement.spacedBy(8.dp))
    ) {
        FavoritesToggle(
            onClick = { setToggle(true) },
            label = "Eateries",
            active = toggle
        )
        FavoritesToggle(
            onClick = { setToggle(false) },
            label = "Items",
            active = !toggle
        )
    }
}


@Preview
@Composable
private fun FavoritesTogglePreview() = EateryPreview {
    var active by remember { mutableStateOf(false) }
    FavoritesToggle({ active = !active }, "Eateries", active)
}