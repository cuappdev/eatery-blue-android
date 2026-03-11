package com.cornellappdev.android.eatery.ui.viewmodels.state

import android.content.Context
import android.widget.Toast
import com.cornellappdev.android.eatery.data.models.NetworkError

/**
 * Centralized handler for displaying network errors to users via Toast.
 */
object NetworkErrorHandler {

    /**
     * Shows a toast message for the given network UI error.
     */
    fun showError(context: Context, error: NetworkUiError?) {
        if (error == null) return

        when (error) {
            is NetworkUiError.Failed -> {
                val message = buildErrorMessage(error.action, error.reason)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Builds a user-friendly error message based on the action and network error reason.
     */
    private fun buildErrorMessage(action: NetworkAction, reason: NetworkError): String {
        val actionDescription = when (action) {
            NetworkAction.AddFavoriteEatery -> "add favorite eatery"
            NetworkAction.RemoveFavoriteEatery -> "remove favorite eatery"
            NetworkAction.AddFavoriteItem -> "add favorite item"
            NetworkAction.RemoveFavoriteItem -> "remove favorite item"
            NetworkAction.UpdateFavorites -> "sync favorites"
            NetworkAction.SendReport -> "send report"
            NetworkAction.LinkGetAccount -> "link account"
            NetworkAction.GetFinancials -> "load account information"
        }

        return "Failed to $actionDescription: $reason"
    }
}



