package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
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
        tint = if (isFavorite) currentColors.favorite else currentColors.accentPrimary,
        modifier = modifier,
        contentDescription = stringResource(
            if (isFavorite) R.string.favorite_button_remove else R.string.favorite_button_add
        )
    )
}