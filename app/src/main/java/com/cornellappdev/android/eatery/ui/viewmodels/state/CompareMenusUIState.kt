package com.cornellappdev.android.eatery.ui.viewmodels.state

import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.Filter

/**
 * Data class representing the UI state for the Compare Menus feature.
 *
 * @property filters The list of filters currently applied to the eateries.
 * @property selected The list of eateries that the user has selected for comparison.
 * @property allEateries The unfiltered list of all available eateries.
 *  This list represents all eateries returned from the repository without any filtering.
 * @property eateries The list of eateries that should be displayed in the UI,
 *  after applying the selected filters. This list is a subset of allEateries,
 *  filtered based on the active filters.
 */

data class CompareMenusUIState(
    val filters : List<Filter> = listOf(),
    val selected: List<Eatery> = listOf(),
    val allEateries : List<Eatery> = listOf(),
    val eateries : List<Eatery> = listOf(),
)