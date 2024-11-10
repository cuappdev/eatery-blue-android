package com.cornellappdev.android.eatery.ui.components.general

import com.cornellappdev.android.eatery.data.models.Eatery

enum class Filter(val text: String) {
    UNDER_10("Under 10 min"),
    NORTH("North"),
    WEST("West"),
    CENTRAL("Central"),
    FAVORITES("Favorites"),
    BRB("BRBs"),
    CASH("Cash or credit"),
    SWIPES("Meal swipes"),
    SELECTED("Selected"),
    TODAY("Today");

    companion object {
        val PAYMENT_METHODS = setOf(BRB, CASH, SWIPES)
        val LOCATIONS = setOf(NORTH, WEST, CENTRAL)
    }
}

fun Filter.passesFilter(
    eatery: Eatery,
    favorites: Map<Int, Boolean>,
    selected: List<Eatery>
): Boolean =
    when (this) {
        Filter.UNDER_10 -> {
            eatery.getWalkTimes()?.let { it <= 10 } ?: false
        }

        Filter.NORTH -> {
            eatery.campusArea == "North"
        }

        Filter.WEST -> {
            eatery.campusArea == "West"
        }

        Filter.CENTRAL -> {
            eatery.campusArea == "Central"
        }

        Filter.FAVORITES -> {
            favorites[eatery.id] == true
        }

        Filter.BRB -> {
            eatery.paymentAcceptsBrbs == true
        }

        Filter.CASH -> {
            eatery.paymentAcceptsCash == true
        }

        Filter.SWIPES -> {
            eatery.paymentAcceptsMealSwipes == true
        }

        Filter.SELECTED -> {
            eatery in selected
        }

        Filter.TODAY -> {
            throw Exception("TODO: redesign filter logic")
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
