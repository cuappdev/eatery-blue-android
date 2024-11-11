package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.colorInterp


@Composable
fun FilterRow(
    filters: List<Filter>,
    currentFiltersSelected: List<Filter>,
    onFilterClicked: (Filter) -> Unit,
    customItemsBefore: LazyListScope.() -> Unit = {},
    customItemsAfter: LazyListScope.() -> Unit = {},
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        customItemsBefore()
        items(filters) { filter ->
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(filter)
                }, selected = filter in currentFiltersSelected,
                text = filter.text
            )
        }
        customItemsAfter()
    }
}

/**
 * One filter button.
 */
@Composable
fun FilterButton(
    onFilterClicked: () -> Unit,
    selected: Boolean,
    text: String,
    icon: ImageVector? = null
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 0f else 1f,
        label = "Button Color",
        animationSpec = tween(150)
    )
    val background = colorInterp(progress, Color.Black, GrayZero)
    val contentColor = colorInterp(progress, Color.White, Color.Black)

    Button(
        onClick = onFilterClicked,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = background,
            contentColor = contentColor
        )
    ) {
        Text(text)
        if (icon != null) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = "Favorite",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}