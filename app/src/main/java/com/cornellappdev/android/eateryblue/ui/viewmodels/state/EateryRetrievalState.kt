package com.cornellappdev.android.eateryblue.ui.viewmodels.state

/**
 * A sealed hierarchy describing the current status of logging in.
 */
sealed interface EateryRetrievalState {
    object Success : EateryRetrievalState
    object Error : EateryRetrievalState
    object Pending : EateryRetrievalState
}
