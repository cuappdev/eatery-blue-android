package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun FavoritesToggle(
    onClick: () -> Unit,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (active) currentColors.borderBlue else currentColors.accentPrimary
    val textColor = if (active) currentColors.contentBrand else currentColors.textSecondary
    val shape = RoundedCornerShape(8.dp)
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(elevation = if (active) 4.dp else 0.dp, shape = shape),
        shape = shape,
        border = BorderStroke(Dp.Hairline, borderColor),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = currentColors.backgroundDefault,
            contentColor = textColor
        )
    ) {
        Text(
            text = label,
            color = textColor,
            style = EateryBlueTypography.button
        )
    }
}

@Composable
fun ToggleRow(toggle: Boolean, setToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FavoritesToggle(
            onClick = { setToggle(true) },
            label = "Eateries",
            active = toggle,
            modifier = Modifier.weight(1f)
        )
        FavoritesToggle(
            onClick = { setToggle(false) },
            label = "Items",
            active = !toggle,
            modifier = Modifier.weight(1f)
        )
    }
}

@DualModePreview
@Composable
private fun FavoritesTogglePreview() = EateryPreview {
    var active by remember { mutableStateOf(false) }
    ToggleRow(toggle = active, setToggle = { active = it })
}


