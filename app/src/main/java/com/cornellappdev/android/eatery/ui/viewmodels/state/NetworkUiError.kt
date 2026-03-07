package com.cornellappdev.android.eatery.ui.viewmodels.state

import com.cornellappdev.android.eatery.data.models.NetworkError

enum class NetworkAction {
    AddFavoriteEatery,
    RemoveFavoriteEatery,
    GetFavorites,
    AddFavoriteItem,
    RemoveFavoriteItem,
    SendReport,
    LinkGetAccount,
    GetFinancials,
}

/**
 * Typed network error payload emitted by ViewModels.
 */
sealed interface NetworkUiError {
    data class Failed(
        val action: NetworkAction,
        val reason: NetworkError,
    ) : NetworkUiError
}
