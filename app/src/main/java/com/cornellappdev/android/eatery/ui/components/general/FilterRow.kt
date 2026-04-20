package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.colorInterp
import com.cornellappdev.android.eatery.ui.viewmodels.ThemeViewModel

@Composable
fun FilterRow(
    filters: List<Filter>,
    currentFiltersSelected: List<Filter>,
    onFilterClicked: (Filter) -> Unit,
    customItemsBefore: LazyListScope.() -> Unit = {},
    customItemsAfter: LazyListScope.() -> Unit = {},
    rowState: LazyListState = rememberLazyListState(),
    themeViewModel : ThemeViewModel = hiltViewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val resolvedDarkMode = isDarkMode ?: isSystemInDarkTheme()
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = rowState
    ) {
        customItemsBefore()
        items(filters) { filter ->
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(filter)
                }, selected = filter in currentFiltersSelected,
                text = filter.text,
                isDarkMode = resolvedDarkMode
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
    hasExpandIcon: Boolean = false,
    isDarkMode: Boolean
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 0f else 1f,
        label = "Button Color",
        animationSpec = tween(150)
    )
    var background = colorInterp(progress, Color.Black, Color(0xFFEFF1F4))
    var contentColor = colorInterp(progress, Color.White, Color.Black)

    if (isDarkMode==true)
    {
        background = colorInterp(progress, Color.White, Color(0xFF272727))
        contentColor = colorInterp(progress, Color.Black, Color.White)
    }



    Button(
        onClick = onFilterClicked,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = contentColor
        )
    ) {
        Text(text)
        if (hasExpandIcon) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = stringResource(R.string.expand_filters),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}