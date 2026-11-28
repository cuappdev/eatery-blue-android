package com.cornellappdev.android.eatery.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * The app's haptic feedback for user actions. Only call these from user-gesture handlers,
 * never for automatic or programmatic state changes.
 */
class EateryHaptics(private val haptic: HapticFeedback) {
    fun favoriteToggled(isFavorite: Boolean) = haptic.performHapticFeedback(
        if (isFavorite) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
    )

    fun refreshStarted() = haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)

    fun refreshCompleted() = haptic.performHapticFeedback(HapticFeedbackType.Confirm)

    fun tabSelected() = haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
}

@Composable
fun rememberEateryHaptics(): EateryHaptics {
    val haptic = LocalHapticFeedback.current
    return remember(haptic) { EateryHaptics(haptic) }
}
