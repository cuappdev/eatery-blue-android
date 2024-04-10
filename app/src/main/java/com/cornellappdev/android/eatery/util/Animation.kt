package com.cornellappdev.android.eatery.util

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

/**
 * A smooth pop-in animation. Derived from AnimatedContent.
 */
@OptIn(ExperimentalAnimationApi::class)
fun popIn(durationMillis: Int = 220, delayMillis: Int = 0) =
    fadeIn(animationSpec = tween(durationMillis, delayMillis = delayMillis)) + scaleIn(
        initialScale = 0.92f, animationSpec = tween(durationMillis, delayMillis = delayMillis)
    )

/**
 * A smooth pop-out animation.
 */
@OptIn(ExperimentalAnimationApi::class)
fun popOut(durationMillis: Int = 220, delayMillis: Int = 0) =
    fadeOut(animationSpec = tween(durationMillis, delayMillis = delayMillis)) + scaleOut(
        targetScale = 0.92f, animationSpec = tween(durationMillis, delayMillis = delayMillis)
    )
