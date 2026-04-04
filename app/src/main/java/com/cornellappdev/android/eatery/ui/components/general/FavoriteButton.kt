package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.currentColors

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoriteIcon(
        isFavorite = isFavorite,
        modifier = modifier
            .padding(top = 3.dp)
            .clip(RoundedCornerShape(9.dp))
            .clickable(
                onClick = {
                    onFavoriteClick(!isFavorite)
                }
            )
    )
}

@Composable
fun FavoriteIcon(isFavorite: Boolean, modifier: Modifier = Modifier) {
    Icon(
        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
        tint = if (isFavorite) Color(0xFFFFD700) else currentColors.accentPrimary,
        modifier = modifier,
        contentDescription = "favorite: $isFavorite"
    )
}