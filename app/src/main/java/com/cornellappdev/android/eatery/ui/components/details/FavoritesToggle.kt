package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.ToggleViewModel
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun ActiveToggle(
    onClick: () -> Unit,
    label: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8))
            .clickable { onClick() }
            .border(BorderStroke(Dp.Hairline, EateryBlue), RoundedCornerShape(8))
            .background(Color.White)
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .size(width = 160.dp, height = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Center content within the Row
    ) {
        Text(
            text = label,
            color = EateryBlue,
            style = EateryBlueTypography.button
        )
    }
}

@Composable
fun InactiveToggle(
    onClick: () -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8))
            .clickable { onClick() }
            .border(BorderStroke(Dp.Hairline, Color(0xFF6B6B6B)), RoundedCornerShape(8))
            .background(GrayZero)
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .size(width = 160.dp, height = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Center content within the Row
    ) {
        Text(
            text = label,
            color = Color(0xFF6B6B6B),
            style = EateryBlueTypography.button
        )
    }
}

@Preview
@Composable
private fun FavoritesTogglePreview() = EateryPreview {
    ActiveToggle ({}, "Eateries" )
    InactiveToggle ({}, "Items" )
}