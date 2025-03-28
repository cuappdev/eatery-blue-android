package com.cornellappdev.android.eatery.ui.components.general

import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.Filter.FromEateryFilter
import java.time.LocalDateTime

data class FilterData(
    val eatery: Eatery? = null,
    val favoriteEateryIds: Map<Int, Boolean>? = null,
    val selectedEateryIds: List<Int>? = null,
    val targetItemName: String? = null,
)


sealed class Filter(open val text: String) {

    /**
     * It was an intentional design decision to make passesFilter protected.
     * This way, you have to go through the passesSelectedFilters method. This method enforces that
     * you provided all the data that all the filters need up front, which makes it harder to
     * accidentally forget to pass data to one of the filters and not notice until a user crashes.
     */
    protected abstract fun passesFilter(filterData: FilterData): Boolean

    data object ItemAvailableToday : Filter(text = "Today") {
        override fun passesFilter(filterData: FilterData): Boolean {
            val itemName = checkNotNull(filterData.targetItemName)
            val eatery = checkNotNull(filterData.eatery)

            return eatery.events?.asSequence()?.filter {
                it.endTime?.let { end ->
                    end < LocalDateTime.now().withHour(23).withMinute(59)
                } == true
            }?.flatMap { it.menu ?: emptyList() }
                ?.flatMap { it.items?.map { item -> item.name } ?: emptyList() }
                ?.contains(itemName) == true
        }
    }

    data object Selected : Filter(text = "Selected") {
        override fun passesFilter(filterData: FilterData): Boolean {
            val selectedEateriesIds = checkNotNull(filterData.selectedEateryIds)
            val eatery = checkNotNull(filterData.eatery)

            return eatery.id in selectedEateriesIds
        }
    }

    sealed class FromEateryFilter(
        override val text: String,
    ) : Filter(text) {
        protected abstract fun passesEateryFilter(eatery: Eatery): Boolean

        override fun passesFilter(filterData: FilterData): Boolean {
            val eatery = checkNotNull(filterData.eatery)
            return passesEateryFilter(eatery)
        }

        data object North : FromEateryFilter(text = "North") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.campusArea == "North"

        }

        data object West : FromEateryFilter(text = "West") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.campusArea == "West"
        }

        data object Central : FromEateryFilter(text = "Central") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.campusArea == "Central"
        }

        data object Under10 : FromEateryFilter(text = "Under 10 min") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.getWalkTimes()?.let { it <= 10 } == true
        }

        data object Swipes : FromEateryFilter(text = "Swipes") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.paymentAcceptsMealSwipes == true
        }

        data object BRB : FromEateryFilter(text = "BRBs") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.paymentAcceptsBrbs == true
        }

        data object Cash : FromEateryFilter(text = "Cash") {
            override fun passesEateryFilter(eatery: Eatery): Boolean =
                eatery.paymentAcceptsCash == true
        }
    }

    sealed class RequiresFavoriteEateries(override val text: String) : Filter(text) {
        data object Favorites : RequiresFavoriteEateries(text = "Favorites") {
            override fun passesFilter(filterData: FilterData): Boolean {
                val favoriteEateryIds = checkNotNull(filterData.favoriteEateryIds)
                val eatery = checkNotNull(filterData.eatery)

                return favoriteEateryIds[eatery.id] == true
            }
        }
    }


    companion object {
        val paymentMethodFilters =
            listOf(FromEateryFilter.Cash, FromEateryFilter.BRB, FromEateryFilter.Swipes)


        /**
         * Checks that the `filterData` passes all of the `selectedFilters`
         * @param allFilters all of the filters that the screen uses, must not be empty
         * @param selectedFilters all of the filters that the user has selected (the filters to
         * check), must be a subset of `allFilters`
         * @param filterData the data for the filter check, requires that enough data is provided
         * such that each filter in `allFilters` can run `passesFilter` with the data
         */
        fun passesSelectedFilters(
            allFilters: List<Filter>,
            selectedFilters: List<Filter>,
            filterData: FilterData
        ): Boolean {
            check(allFilters.isNotEmpty())
            check(allFilters.containsAll(selectedFilters))
            checkFilterContext(allFilters, filterData)
            return selectedFilters.all { it.passesFilter(filterData) }
        }

        /**
         * Verifies that the `filterContext` has enough information for the filters that could be
         * used.
         * @throws IllegalStateException if `filterContext` has `null` fields that the filters in
         * `allFilters` require
         */
        private fun checkFilterContext(
            allFilters: List<Filter>,
            filterData: FilterData
        ) {
            allFilters.forEach {
                it.passesFilter(filterData)
            }
        }
    }
}

fun List<Filter>.updateFilters(newFilter: Filter): List<Filter> {
    if (newFilter in this) {
        return this - newFilter
    }
    val locationFilters =
        listOf(FromEateryFilter.Central, FromEateryFilter.West, FromEateryFilter.North)
    if (newFilter in locationFilters
    ) {
        return (this - locationFilters.toSet()) + newFilter
    }
    return this + newFilter
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
