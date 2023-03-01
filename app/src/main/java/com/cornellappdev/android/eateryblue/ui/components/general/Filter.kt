package com.cornellappdev.android.eateryblue.ui.components.general

enum class Filter(val text: String) {
    UNDER_10("Under 10 min"),
    NORTH("North"),
    WEST("West"),
    CENTRAL("Central"),
    ALL_CAMPUS("All Campus"),
    FAVORITES("Favorites"),
    BRB("BRBs"),
    CASH("Cash or credit"),
    SWIPES("Meal swipes"),
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner");

    companion object {
        val PAYMENT_METHODS = setOf(BRB, CASH, SWIPES)
        val MEALS = setOf(BREAKFAST, LUNCH, DINNER)
        val LOCATIONS = setOf(ALL_CAMPUS, NORTH, WEST, CENTRAL)
    }
}
