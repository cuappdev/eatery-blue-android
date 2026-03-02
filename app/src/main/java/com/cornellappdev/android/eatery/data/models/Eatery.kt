package com.cornellappdev.android.eatery.data.models

import android.location.Location
import androidx.compose.ui.graphics.Color
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.util.Constants.AVERAGE_WALK_SPEED
import com.cornellappdev.android.eatery.util.LocationHandler
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@JsonClass(generateAdapter = true)
data class Eatery(
    @Json(name = "cornellId") val id: Int? = null,
    val announcements: List<String>? = null,
    val name: String? = null,
    val shortName: String? = null,
    val about: String? = null,
    val shortAbout: String? = null,
    val cornellDining: Boolean? = null,
    val menuSummary: String? = null,
    val imageUrl: String? = null,
    val campusArea: String? = null,
    val onlineOrderUrl: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val location: String? = null,
    val paymentMethods: List<PaymentMethod>? = null,
    val eateryTypes: List<String>? = null,
    val createdAt: LocalDateTime? = null,
    val events: List<Event>? = null,
    val waitTimes: List<WaitTimeDay>? = null,
    val alerts: List<Alert>? = null,
) {
    fun getWalkTimes(): Int? {
        val currentLocation = LocationHandler.currentLocation.value
        val results = floatArrayOf(0f)
        if (latitude == null || longitude == null || currentLocation == null) {
            return null
        }
        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            latitude.toDouble(),
            longitude.toDouble(),
            results
        )
        return ((results[0] / AVERAGE_WALK_SPEED) / 60).toInt()
    }


    private fun getTodaysEvents(): List<Event> {
        val currentTime = LocalDateTime.now()
        if (events.isNullOrEmpty())
            return listOf()

        val todayEvents = events.filter { event ->
            currentTime.dayOfYear == event.startTimestamp?.dayOfYear
        }.sortedBy { it.startTimestamp }
        // is sorting them here too slow?
        todayEvents.forEach { event ->
            var i = 0
            val chefs: MutableList<MenuCategory> = mutableListOf()
            event.menu?.forEach { menuCategory ->
                if (menuCategory.name != null && menuCategory.name == "Chef's Table") {
                    val chef = event.menu[i]
                    chefs.add(chef)
                } else if (menuCategory.name != null && menuCategory.name == "Chef's Table - Sides") {
                    val chef = event.menu[i]
                    chefs.add(0, chef)
                }
                i++
            }
            chefs.forEach { menuCategory ->
                event.menu?.remove(menuCategory)
                event.menu?.add(0, menuCategory)
            }
        }

        return todayEvents
    }

    /**
     * Returns the event that should be displayed at the Ithaca local time
     * If there is currently a meal going on, that is displayed
     * If no meal is going on, the next meal is displayed
     * If the last meal of the day has passed, display the last meal of the day.
     *
     * If there are no events whatsoever in the day, null is returned. This should be the only case
     * that returns null.
     */
    fun getCurrentDisplayedEvent(): Event? {
        val now = LocalDateTime.now()
        val todayEvents = getTodaysEvents()
        val currentEvent = todayEvents.find { event ->
            (event.startTimestamp?.isBefore(now) ?: true) && (event.endTimestamp?.isAfter(now)
                ?: true)
        }
        return currentEvent ?: todayEvents.find { it.startTimestamp?.isAfter(now) ?: true }
        ?: todayEvents.lastOrNull()
    }


    /**
     * @returns the event that makes the day index and mealDescription
     *
     * @param dayIndex, the index of the selected day, today is 0, tomorrow is 1, and so on
     * @param mealDescription, e.g. "lunch", "dinner", etc
     */
    fun getSelectedEvent(dayIndex: Int, mealDescription: String): Event? {
        val targetDate = LocalDate.now().plusDays(dayIndex.toLong())

        val ans = events?.find {
            it.type.equals(mealDescription, ignoreCase = true) &&
                    (it.startTimestamp?.toLocalDate()?.isEqual(targetDate) == true)
        }
        return ans
    }

    /**
     * @return the association list of mealDescription of one eatery on one day based
     * on chronological order and the duration of that particular meal
     * e.g. for Oken on Mondays, it would return
     * [("Lunch", some string duration),("Dinner", some string duration)]
     * note, for cafes, it would just return [("Open",some string duration)],
     * for louies, it returns [("General",some string duration)]
     * Note, string duration are in the format "11:00 AM - 2:30 PM"
     */
    fun getTypeMeal(currSelectedDay: DayOfWeek): List<Pair<String, String>> {
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

        val uniqueMeals = LinkedHashMap<String, String>()

        events?.filter { it.startTimestamp?.dayOfWeek == currSelectedDay }
            ?.forEach { event ->
                val description = event.type
                val startTime = event.startTimestamp
                val endTime = event.endTimestamp
                if (description != null && startTime != null && endTime != null && !uniqueMeals.containsKey(
                        description
                    )
                ) {
                    val durationString =
                        "${startTime.format(timeFormatter)} - ${endTime.format(timeFormatter)}"
                    uniqueMeals[description] = durationString
                }
            }

        return uniqueMeals.toList()
    }

    /**
     * Returns the list of DayOfWeek that this eatery is closed
     */
    fun getClosedDays(): List<DayOfWeek> {
        val dailyHours = operatingHours()

        return dailyHours.filter { (_, times) ->
            "Closed" in times
        }.map { (day, _) ->
            day
        }
    }

    fun getSelectedDayMeal(meal: MealFilter, day: Int): List<Event>? {
        var currentDay = LocalDate.now()
        currentDay = currentDay.plusDays(day.toLong())
        return events?.filter { event ->
            currentDay.dayOfYear == event.startTimestamp?.dayOfYear && meal.text.contains(event.type)
        }
    }

    private fun getCurrentEvents(): List<Event> {
        val currentTime = LocalDateTime.now()
        if (events.isNullOrEmpty())
            return listOf()

        return events.filter { event ->
            (currentTime.isAfter(event.startTimestamp) || currentTime.isEqual(event.startTimestamp)) && (currentTime.isBefore(
                event.endTimestamp
            ) || currentTime.isEqual(event.endTimestamp))
        }
    }


    fun getOpenUntil(): String? {
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return null

        val endTime = currentEvents.first().endTimestamp ?: return null
        return "${endTime.format(DateTimeFormatter.ofPattern("K:mm a"))}"
    }

    fun isClosed(): Boolean {
        return getOpenUntil() == null
    }

    /**
     * Returns true if the eatery has a current event and that event is ending within [minutes].
     */
    fun isClosingSoon(minutes: Int = 60): Boolean {
        // TODO: Make a more well designed StateFlow for the open state of an Eatery
        //  This should encompass Open, Closing Soon, and Closed.
        val currentTime = LocalDateTime.now()
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return false

        val endTime = currentEvents.first().endTimestamp
        val timeBuffer: Long = Duration.between(currentTime, endTime).toMinutes()

        return timeBuffer < minutes
    }

    fun calculateTimeUntilClosing(): StateFlow<Int>? {
        val currentTime = LocalDateTime.now()
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return null
        val endTime = currentEvents.first().endTimestamp ?: return null
        var timeBuffer: Long = Duration.between(currentTime, endTime).toMinutes()

        return flow {
            while (timeBuffer in 1..60) {
                timeBuffer = Duration.between(currentTime, endTime).toMinutes()
                emit(timeBuffer.toInt())
                delay(
                    /**45 seconds=*/
                    45000
                )
            }
        }.stateIn(
            CoroutineScope(Dispatchers.Default),
            SharingStarted.Eagerly,
            timeBuffer.toInt()
        )
    }

    fun acceptsMealSwipes(): Boolean = paymentMethods?.contains(PaymentMethod.MEAL_SWIPE) ?: false

    fun acceptsCard(): Boolean = paymentMethods?.contains(PaymentMethod.CARD) ?: false

    fun acceptsCash(): Boolean = paymentMethods?.contains(PaymentMethod.CASH) ?: false

    fun acceptsBRB(): Boolean = paymentMethods?.contains(PaymentMethod.BRB) ?: false

//    fun acceptsMealSwipes(): Boolean = paymentMethods?.contains("MEAL_SWIPE") ?: false
//
//    fun acceptsCard(): Boolean = paymentMethods?.contains("CARD") ?: false
//
//    fun acceptsCash(): Boolean = paymentMethods?.contains("CASH") ?: false
//
//    fun acceptsBRB(): Boolean = paymentMethods?.contains("BRB") ?: false

    /**
     * Private helper function that returns a map of the day of week that a eatery is open
     * to the opening time(s) or closed status (these are strings)
     *
     * e.g. For Oken, {Monday -> ["11:00 AM - 2:30 PM", "4:30 PM - 9:00 PM"], Sunday -> "Closed"}
     */
    private fun operatingHours(): Map<DayOfWeek, MutableList<String>> {
        val dailyHours = mutableMapOf<DayOfWeek, MutableList<String>>()

        events?.forEach { event ->
            val dayOfWeek = event.startTimestamp?.dayOfWeek
            val openTime = event.startTimestamp?.format(DateTimeFormatter.ofPattern("h:mm a"))
            val closeTime = event.endTimestamp?.format(DateTimeFormatter.ofPattern("h:mm a"))
            val timeString = "$openTime - $closeTime"

            if (dayOfWeek != null && dailyHours[dayOfWeek]?.none { it.contains(timeString) } != false) {
                dailyHours.computeIfAbsent(dayOfWeek) { mutableListOf() }.add(timeString)
            }
        }

        DayOfWeek.entries.forEach { dayOfWeek ->
            dailyHours.computeIfAbsent(dayOfWeek) { mutableListOf("Closed") }
        }

        return dailyHours
    }

    /**@Return a list of pairs (association list) representing the day(s) of a week
     * and the corresponding times that a eatery is open
     *
     * this is computed by first mapping each dayOfWeek in each element of events to
     * corresponding opening times (with helper operatingHours()),
     * then a helper (groupedHoursFormatHelper) to group
     * daysOfWeek with the same list of opening times into the association list of
     * day(s) mapped to opening hours.
     */
    fun formatOperatingHours(): List<Pair<String, List<String>>> {
        val dailyHours = operatingHours()

        val groupedHours = dailyHours.entries.groupBy({ it.value }, { it.key })

        return groupedHoursFormatHelper(groupedHours)
    }

    /**
     * value to represent the custom order of days in a week (with Sunday as
     * the first day due to a particular design choice). Used for sorting purposes
     */
    private val dayOrder = mapOf(
        "Sunday" to 1,
        "Monday" to 2,
        "Tuesday" to 3,
        "Wednesday" to 4,
        "Thursday" to 5,
        "Friday" to 6,
        "Saturday" to 7

    )

    /**
     * @Return a list of pairs (association list) representing the day(s) of a week
     * and the corresponding times that a eatery is open. The list of pairs is sorted
     * by key (day(s)) with the custom dayOrder
     *
     * @Param groupedHours: a Map data structure mapping a MutableList of strings
     * representing the opening times of a eatery to the days that opens at those
     * opening times
     *
     * The function groups consecutive days of the week that share the same opening time
     * together; then, these groups, along with days with unique opening times compared
     * to its neighbor days, are each mapped to the corresponding opening times
     */
    private fun groupedHoursFormatHelper(groupedHours: Map<MutableList<String>, List<DayOfWeek>>): List<Pair<String, List<String>>> {
        val formattedHours = LinkedHashMap<String, List<String>>()

        groupedHours.forEach { entry ->
            val days = entry.value.sortedBy {
                val dayName = "$it".take(1).uppercase() + "$it".drop(1).lowercase()
                dayOrder[dayName] ?: Int.MAX_VALUE
            }
            var curStrings = mutableListOf<String>()
            for (i in (days.indices)) {
                val curDay = "${days[i]}".take(1).uppercase() + "${days[i]}".drop(1).lowercase()
                if (i == days.size - 1) {
                    curStrings.add(curDay)
                    val formattedString = formatString(curStrings)
                    formattedHours[formattedString] = entry.key
                } else {
                    curStrings.add(curDay)
                    if (!isNext("${days[i]}", "${days[i + 1]}")) {
                        val formattedString = formatString(curStrings)
                        formattedHours[formattedString] = entry.key
                        curStrings = mutableListOf()
                    }
                }
            }
        }

        val formattedHoursList = formattedHours.toList().sortedBy { entry ->
            val firstDay = entry.first.split(" to ", " ", limit = 2).first()
            dayOrder[firstDay] ?: Int.MAX_VALUE
        }
        return formattedHoursList
    }

    /**
     * @Return Boolean representing if day2 is right (consecutively) after
     * day1
     *
     * @Param day1: a all capitalized string representing a day in the week
     * @Param day2: a all capitalized string representing a day in the week
     */
    private fun isNext(day1: String, day2: String): Boolean {
        return when (day1) {
            "MONDAY" -> day2 == "TUESDAY"
            "TUESDAY" -> day2 == "WEDNESDAY"
            "WEDNESDAY" -> day2 == "THURSDAY"
            "THURSDAY" -> day2 == "FRIDAY"
            "FRIDAY" -> day2 == "SATURDAY"
            "SATURDAY" -> false
            "SUNDAY" -> day2 == "MONDAY"
            else -> false
        }
    }

    /**
     * @Return formatted string representing the duration of days of a week
     *
     * @Param strings a list of strings representing day(s) that need to be formatted
     *
     * Given a list of strings, format it in the format FirstDay to LastDay.
     * If the list of string only contains one string, then just return
     * that one string element.
     */
    private fun formatString(strings: List<String>): String {
        return when {
            strings.size > 1 -> "${strings.first()} to ${strings.last()}"
            else -> strings.first()
        }
    }
}


@JsonClass(generateAdapter = true)
data class Alert(
    val id: Int? = null,
    val description: String? = null,
    val startTimestamp: LocalDateTime? = null,
    val endTimestamp: LocalDateTime? = null
)

@JsonClass(generateAdapter = true)
data class WaitTimeDay(
    val canonicalDate: Date? = null,
    val data: List<WaitTimeData>? = null
)


@JsonClass(generateAdapter = true)
data class WaitTimeData(
    val timestamp: LocalDateTime? = null,
    val waitTimeLow: Int? = null,
    val waitTimeExpected: Int? = null,
    val waitTimeHigh: Int? = null
)

@JsonClass(generateAdapter = true)
enum class PaymentMethod {
    CASH,
    MEAL_SWIPE,
    CARD,
    BRB,
    FREE,
    UNKNOWN
}


@JsonClass(generateAdapter = true)
data class Event(
    val id: Int? = null,
    /**
     * "Lunch", "Dinner", etc…
     */
    val type: String? = null,
    val startTimestamp: LocalDateTime? = null,
    val endTimestamp: LocalDateTime? = null,
    val upvotes: Int? = null,
    val downvotes: Int? = null,
    val createdAt: LocalDateTime? = null,
    val eateryId: Int? = null,
    val menu: MutableList<MenuCategory>? = null,

    )

@JsonClass(generateAdapter = true)
data class MenuCategory(
    val id: Int? = null,
    val name: String? = null,
    val createdAt: LocalDateTime? = null,
    val eventId: Int? = null,
    val items: List<MenuItem>? = null,
)

@JsonClass(generateAdapter = true)
data class MenuItem(
    val id: Int? = null,
    val name: String? = null,
    val basePrice: Double? = null,
    val createdAt: LocalDateTime? = null,
    val categoryId: Int? = null,
    val dietaryPreferences: List<String>? = null,
    val allergens: List<String>? = null
)

data class EateryStatus(
    val statusText: String,
    val statusColor: Color,
)
