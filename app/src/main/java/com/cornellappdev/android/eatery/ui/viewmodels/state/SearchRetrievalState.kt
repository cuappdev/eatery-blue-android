package com.cornellappdev.android.eatery.ui.viewmodels.state

import com.cornellappdev.android.eatery.data.models.Eatery

/**
 * A sealed hierarchy describing the current status of searching for eateries.
 */
sealed interface SearchRetrievalState {
    data class Success(val searchResults: List<Eatery>) : SearchRetrievalState
    object Error : SearchRetrievalState
    object Pending : SearchRetrievalState
}
