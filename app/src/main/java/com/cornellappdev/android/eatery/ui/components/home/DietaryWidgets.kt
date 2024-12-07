package com.cornellappdev.android.eatery.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Red

/**
 * Dietary widgets that are displayed at the bottom left corner of Eatery Card
 */
@Composable
fun DietaryWidgets(eatery: Eatery, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable {
            onClick.invoke()
        }, shape = CircleShape, color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)
        ) {
            //TODO once backend has each dietary attribute for an Eatery, change
            //the if statement content to if(eatery.vegan)
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_vegan),
                    contentDescription = "vegan",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_peanut_free),
                    contentDescription = "peanut free",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_diary_free),
                    contentDescription = "diary free",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_vegetarian),
                    contentDescription = "vegetarian",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gluten_free),
                    contentDescription = "gluten free",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_halal),
                    contentDescription = "halal",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nut_free),
                    contentDescription = "nut free",
                    tint = Color.Unspecified
                )
            }
            if (true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_kosher),
                    contentDescription = "kosher",
                    tint = Color.Unspecified
                )
            }
        }
    }
}