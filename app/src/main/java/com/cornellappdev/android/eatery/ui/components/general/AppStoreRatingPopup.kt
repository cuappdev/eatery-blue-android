package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Dialog
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.appStorePopupRepository

@Composable
fun AppStoreRatingPopup(
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository()
) {
    val showPopup = appStorePopupRepository.popupShowing.collectAsState().value
    if (showPopup) {
        Dialog(appStorePopupRepository::dismissPopup) {
            Text("Help me")
            Button(appStorePopupRepository::dismissPopup) { Text("Dismiss") }
        }
    }
}