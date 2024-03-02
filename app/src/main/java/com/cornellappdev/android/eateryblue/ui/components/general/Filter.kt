package com.cornellappdev.android.eateryblue.ui.components.general

enum class Filter(val text: String) {
    UNDER_10("Under 10 min"),
    NORTH("North"),
    WEST("West"),
    CENTRAL("Central"),
    FAVORITES("Favorites"),
    BRB("BRBs"),
    CASH("Cash or credit"),
    SWIPES("Meal swipes");

    companion object {
        val PAYMENT_METHODS = setOf(BRB, CASH, SWIPES)
        val LOCATIONS = setOf(NORTH, WEST, CENTRAL)
    }
}

enum class MealFilter(val text: String, val endTime: Float) {
    BREAKFAST("Breakfast", 10.5f),
    LUNCH("Lunch", 14f),
    LATELUNCH("Late Lunch", 16.5f),
    DINNER("Dinner", 20.5f),
    LATEDINNER("Late Dinner", 10.5f);
}
