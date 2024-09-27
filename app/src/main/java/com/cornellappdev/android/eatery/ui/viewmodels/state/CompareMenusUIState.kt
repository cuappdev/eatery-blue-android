package com.cornellappdev.android.eatery.ui.viewmodels.state

import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.Filter

data class CompareMenusUIState(
    val filters : List<Filter> = listOf(),
    val selected: List<Eatery> = listOf(),
    val allEateries : List<Eatery> = listOf(),
    val eateries : List<Eatery> = listOf(),
)