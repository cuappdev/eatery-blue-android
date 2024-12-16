package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.data.models.MenuCategory
import com.cornellappdev.android.eatery.data.models.MenuItem
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero

data class MenuCategoryViewState(
    val category: String,
    val items: List<MenuItemViewState>
) {
    fun toMenuCategory() = MenuCategory(
        category = category,
        items = items.map { it.toMenuItem() }
    )
}


data class MenuItemViewState(
    val isFavorite: Boolean,
    val item: MenuItem,
) {
    fun toMenuItem() = item
}

typealias ItemName = String

fun LazyListScope.menuItems(
    items: List<MenuCategoryViewState>,
    onFavoriteClick: (ItemName) -> Unit,
) {
    items.forEach { category ->
        item {
            Text(
                text = category.category,
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            )
        }

        itemsIndexed(category.items) { index, menuItem ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = menuItem.item.name ?: "Item Name",
                    style = EateryBlueTypography.button,
                    modifier = Modifier.weight(1f)
                )
                FavoriteButton(menuItem.isFavorite, onFavoriteClick = {
                    menuItem.item.name?.let {
                        onFavoriteClick(it)
                    }
                })
            }

            if (category.items.lastIndex != index) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayZero, CircleShape)
                )
            }
            if (category.items.lastIndex == index) {
                Divider(
                    color = GrayZero,
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}