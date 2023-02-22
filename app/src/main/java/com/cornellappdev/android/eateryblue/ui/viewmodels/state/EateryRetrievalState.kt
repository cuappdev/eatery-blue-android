package com.cornellappdev.android.eateryblue.ui.viewmodels.state

/**
 * A sealed hierarchy describing the current status of retrieving any eateries.
 */
sealed interface EateryRetrievalState {
    object Success : EateryRetrievalState
    object Error : EateryRetrievalState
    object Pending : EateryRetrievalState
}
