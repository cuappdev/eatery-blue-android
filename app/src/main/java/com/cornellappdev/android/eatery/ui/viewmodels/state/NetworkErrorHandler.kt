package com.cornellappdev.android.eatery.ui.viewmodels.state

import android.content.Context
import android.widget.Toast
import com.cornellappdev.android.eatery.R
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
                val message = buildErrorMessage(context, error.action, error.reason)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Builds a user-friendly error message based on the action and network error reason.
     */
    private fun buildErrorMessage(
        context: Context,
        action: NetworkAction,
        reason: NetworkError
    ): String {
        val actionDescriptionRes = when (action) {
            NetworkAction.AddFavoriteEatery -> R.string.network_action_add_favorite_eatery
            NetworkAction.RemoveFavoriteEatery -> R.string.network_action_remove_favorite_eatery
            NetworkAction.AddFavoriteItem -> R.string.network_action_add_favorite_item
            NetworkAction.RemoveFavoriteItem -> R.string.network_action_remove_favorite_item
            NetworkAction.UpdateFavorites -> R.string.network_action_update_favorites
            NetworkAction.SendReport -> R.string.network_action_send_report
            NetworkAction.LinkGetAccount -> R.string.network_action_link_get_account
            NetworkAction.GetFinancials -> R.string.network_action_get_financials
        }

        return context.getString(
            R.string.network_error_message,
            context.getString(actionDescriptionRes),
            reason
        )
    }
}



