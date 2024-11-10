package com.cornellappdev.android.eatery.ui.components.general

import com.cornellappdev.android.eatery.data.models.Eatery

sealed class Filter {
    sealed class CustomFilter : Filter() {
        data object Today : CustomFilter()
        data object Selected : CustomFilter()
    }

    sealed class FromEatery : Filter() {
        data object North : FromEatery()
        data object West : FromEatery()
        data object Central : FromEatery()
        data object Under10 : FromEatery()
        data object Swipes : FromEatery()
        data object BRB : FromEatery()
        data object Cash : FromEatery()
    }

    sealed class RequiresFavoriteEateries : Filter() {
        data object Favorites : RequiresFavoriteEateries()
    }
}

fun Filter.RequiresFavoriteEateries.passesFilter(
    eatery: Eatery,
    favorites: Map<Int, Boolean>
): Boolean {
    return favorites[eatery.id] == true
}


fun Filter.FromEatery.passesFilter(
    eatery: Eatery,
): Boolean =
    when (this) {
        Filter.FromEatery.Under10 -> {
            eatery.getWalkTimes()?.let { it <= 10 } ?: false
        }

        Filter.FromEatery.North -> {
            eatery.campusArea == "North"
        }

        Filter.FromEatery.West -> {
            eatery.campusArea == "West"
        }

        Filter.FromEatery.Central -> {
            eatery.campusArea == "Central"
        }


        Filter.FromEatery.BRB -> {
            eatery.paymentAcceptsBrbs == true
        }

        Filter.FromEatery.Cash -> {
            eatery.paymentAcceptsCash == true
        }

        Filter.FromEatery.Swipes -> {
            eatery.paymentAcceptsMealSwipes == true
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
