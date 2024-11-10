package com.cornellappdev.android.eatery.ui.components.general

import com.cornellappdev.android.eatery.data.models.Eatery

sealed class Filter(open val text: String) {
    sealed class CustomFilter(override val text: String) : Filter(text) {
        data object Today : CustomFilter(text = "Today")
        data object Selected : CustomFilter(text = "Selected")
    }

    sealed class FromEatery(override val text: String) : Filter(text) {
        data object North : FromEatery(text = "North")
        data object West : FromEatery(text = "West")
        data object Central : FromEatery(text = "Central")
        data object Under10 : FromEatery(text = "Under 10 min")
        data object Swipes : FromEatery(text = "Swipes")
        data object BRB : FromEatery(text = "BRBs")
        data object Cash : FromEatery(text = "Cash")
    }

    sealed class RequiresFavoriteEateries(override val text: String) : Filter(text) {
        data object Favorites : RequiresFavoriteEateries(text = "Favorites")
    }

    companion object {
        val paymentMethodFilters = listOf(FromEatery.Cash, FromEatery.BRB, FromEatery.Swipes)
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
