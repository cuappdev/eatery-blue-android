package com.cornellappdev.android.eatery.data.models

/** How a meal swipe plan's balance is replenished, as reported by the backend's `plan` field. */
enum class MealSwipePlan {
    UNLIMITED,
    WEEKLY,
    SEMESTER;

    companion object {
        fun from(plan: String?): MealSwipePlan? =
            entries.firstOrNull { it.name.equals(plan, ignoreCase = true) }
    }
}
