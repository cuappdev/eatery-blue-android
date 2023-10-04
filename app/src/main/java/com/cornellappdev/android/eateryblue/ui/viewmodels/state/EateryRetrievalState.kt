package com.cornellappdev.android.eateryblue.ui.viewmodels.state

import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse.Error
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse.Pending
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse.Success

// TODO: Delete this, and replace it with [EateryApiResponse]. Also, replace all occurrences of this
//  with a Flow-based networking model. See EateryDetailViewModel for an example of using flow
//  mapping / combination to best fit with Jetpack Compose.
sealed class EateryRetrievalState {
    object Pending : EateryRetrievalState()
    object Error : EateryRetrievalState()
    object Success : EateryRetrievalState()
}

/**
 * Represents the state of an api response fetching data of type [T].
 * Can be: [Pending], which represents the call still loading in, [Error], which represents the
 * API call failing, and [Success], which contains a `data` field containing the [T] data.
 */
sealed class EateryApiResponse<out T : Any> {
    object Pending : EateryApiResponse<Nothing>()
    object Error : EateryApiResponse<Nothing>()
    class Success<out T : Any>(val data: T) : EateryApiResponse<T>()
}
