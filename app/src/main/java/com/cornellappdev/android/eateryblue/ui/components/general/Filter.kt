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

/**
 * MealFilter enum class which contains enums representing a meal time
 * text: List of names for meals relevant to that filter (Ex. Breakfast filter shows both Breakfast and Brunch options)
 * endTimes: Float that represents average end time for meal out of 24
 */
enum class MealFilter(val text: List<String>, val endTimes: Float) {
    BREAKFAST(listOf("Breakfast", "Brunch"), 10.5f),
    LUNCH(listOf("Lunch", "Brunch", "Late Lunch"), 16f),
    DINNER(listOf("Dinner"), 20.5f),
    LATE_DINNER(listOf("Late Night"), 22.5f);
}
