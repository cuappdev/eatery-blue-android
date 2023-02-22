package com.cornellappdev.android.eateryblue.ui.viewmodels.state

import com.cornellappdev.android.eateryblue.data.models.Eatery

/**
 * A sealed hierarchy describing the current status of searching for eateries.
 */
sealed interface SearchRetrievalState {
    data class Success(val searchResults: List<Eatery>) : SearchRetrievalState
    object Error : SearchRetrievalState
    object Pending : SearchRetrievalState
}
