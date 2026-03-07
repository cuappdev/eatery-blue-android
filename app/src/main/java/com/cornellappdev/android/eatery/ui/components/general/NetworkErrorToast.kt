package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkErrorHandler
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError

/**
 * Observes network errors and displays them via Toast.
 * Call this from your screen composables.
 *
 * @param error The current error state from the ViewModel
 * @param onErrorShown Callback invoked after showing the error (typically to clear it)
 */
@Composable
fun NetworkErrorToast(
    error: NetworkUiError?,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(error) {
        if (error != null) {
            NetworkErrorHandler.showError(context, error)
            onErrorShown()
        }
    }
}


