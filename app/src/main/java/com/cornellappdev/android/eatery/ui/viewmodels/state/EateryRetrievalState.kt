package com.cornellappdev.android.eatery.ui.viewmodels.state

import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse.Error
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse.Pending
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse.Success

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
