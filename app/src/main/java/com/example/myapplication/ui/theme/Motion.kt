package com.example.myapplication.ui.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing

/**
 * Centralized motion specifications for consistent durations and easings across FitTrack.
 */
object Motion {
    const val DURATION_SHORT = 200
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 400

    val EASING_STANDARD: Easing = FastOutSlowInEasing
}
