package com.cornellappdev.android.eateryblue.data.models

import android.location.Location
import android.util.Log
import com.cornellappdev.android.eateryblue.ui.components.general.MealFilter
import com.cornellappdev.android.eateryblue.util.Constants.AVERAGE_WALK_SPEED
import com.cornellappdev.android.eateryblue.util.LocationHandler
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date

@JsonClass(generateAdapter = true)
data class Eatery(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "menu_summary") val menuSummary: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "campus_area") val campusArea: String? = null,
    @Json(name = "online_order_url") val onlineOrderUrl: String? = null,
    @Json(name = "latitude") val latitude: Float? = null,
    @Json(name = "longitude") val longitude: Float? = null,
    @Json(name = "payment_accepts_meal_swipes") val paymentAcceptsMealSwipes: Boolean? = null,
    @Json(name = "payment_accepts_brbs") val paymentAcceptsBrbs: Boolean? = null,
    @Json(name = "payment_accepts_cash") val paymentAcceptsCash: Boolean? = null,
    @Json(name = "events") val events: List<Event>? = null,
    @Json(name = "wait_times") val waitTimes: List<WaitTimeDay>? = null,
    @Json(name = "alerts") val alerts: List<Alert>? = null,
) {
    fun getWalkTimes(): Int? {
        val currentLocation = LocationHandler.currentLocation
        val results = floatArrayOf(0f)
        if (latitude == null || longitude == null || currentLocation == null)
            return null
        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            latitude.toDouble(),
            longitude.toDouble(),
            results
        )
        return ((results[0] / AVERAGE_WALK_SPEED) / 60).toInt()
    }

    fun getWaitTimes(): String? {
        if (waitTimes.isNullOrEmpty())
            return null

        val waitTimeDay = waitTimes.find { waitTimeDay ->
            // checks if today is the right day
            waitTimeDay.canonicalDate
                ?.toInstant()
                ?.truncatedTo(ChronoUnit.DAYS)
                ?.equals(Date().toInstant().truncatedTo(ChronoUnit.DAYS)) ?: true
        }?.data

        val waitTimes: WaitTimeData? = waitTimeDay?.find { waitTimeData ->
            waitTimeData.timestamp?.isBefore(LocalDateTime.now()) == true
        }

        return if (waitTimes != null) {
            "${waitTimes.waitTimeLow?.div(60)}-${waitTimes.waitTimeHigh?.div(60)}"
        } else {
            null
        }
    }


    fun getTodaysEvents(): List<Event> {
        val currentTime = LocalDateTime.now()
        if (events.isNullOrEmpty())
            return listOf()

        val todayEvents = events.filter { event ->
            currentTime.dayOfYear == event.startTime?.dayOfYear
        }.sortedBy { it.startTime }
        // is sorting them here too slow?
        todayEvents.forEach { event ->
            var i = 0
            val chefs: MutableList<MenuCategory> = mutableListOf()
            event.menu?.forEach { menuCategory ->
                if (menuCategory.category != null && menuCategory.category == "Chef's Table") {
                    val chef = event.menu[i]
                    chefs.add(chef)
                } else if (menuCategory.category != null && menuCategory.category == "Chef's Table - Sides") {
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
     * Returns the currently active event, or null if no event is active.
     *
     * Example: At 1 PM, Morrison will return the lunch event.
     */
    fun getCurrentEvent(): Event? {
        return getTodaysEvents().find {
            it.startTime?.isBefore(LocalDateTime.now()) ?: true
                    && it.endTime?.isAfter(LocalDateTime.now()) ?: true
        }
    }

    fun getCurrentDisplayedEvent(): Event {
        val now = LocalDateTime.now()
        val todayEvents = getTodaysEvents()
        val currentEvent = todayEvents.find { event ->
            (event.startTime?.isBefore(now) ?: true) && (event.endTime?.isAfter(now) ?: true)
        }
        return currentEvent ?: todayEvents.find { it.startTime?.isAfter(now) ?: true } ?: todayEvents.last()
    }


    /**
     * @returns the event that makes the day index and mealDescription
     *
     * @param dayIndex, the index of the selected day, today is 0, tomorrow is 1, and so on
     * @param mealDescription, e.g. "lunch", "dinner", etc
     */
    fun getSelectedEvent(dayIndex: Int, mealDescription : String) : Event?{
        //todo
        val todayEvents = getTodaysEvents()
        return todayEvents.find { true }
    }

    /**
     * @returns the list of mealDescription of one eatery on one day based on chronological order
     * e.g. for Oken, it would return ["lunch","dinner"]
     *
     * note, for cafes, it would just return ["open"]
     */
    fun getTypeMeal(currSelectedDay : Int) : List<String?>? {
        return events?.groupBy { it.description }?.keys?.toList() ?: null
    }

    fun getSelectedDayMeal(meal: MealFilter, day: Int): List<Event>? {
        var currentDay = LocalDate.now()
        currentDay = currentDay.plusDays(day.toLong())
        Log.d(name, events?.filter { currentDay.dayOfYear == it.startTime?.dayOfYear }.toString())
        return events?.filter { event ->
            currentDay.dayOfYear == event.startTime?.dayOfYear && meal.text.contains(event.description)
        }
    }

    private fun getCurrentEvents(): List<Event> {
        val currentTime = LocalDateTime.now()
        if (events.isNullOrEmpty())
            return listOf()

        return events.filter { event ->
            (currentTime.isAfter(event.startTime) || currentTime.isEqual(event.startTime)) && (currentTime.isBefore(
                event.endTime
            ) || currentTime.isEqual(event.endTime))
        }
    }


    fun getOpenUntil(): String? {
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return null

        val endTime = currentEvents.first().endTime ?: return null
        return "${endTime.format(DateTimeFormatter.ofPattern("K:mm a"))}"
    }

    fun isClosed(): Boolean {
        return getOpenUntil() == null
    }

    fun isClosingInTen(): Boolean {
        val currentTime = LocalDateTime.now()
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return false

        val endTime = currentEvents.first().endTime ?: return false
        return currentTime.plusMinutes(10).isAfter(endTime)
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

        val endTime = currentEvents.first().endTime
        val timeBuffer: Long = Duration.between(currentTime, endTime).toMinutes()

        return timeBuffer < minutes
    }

    fun calculateTimeUntilClosing(): StateFlow<Int>? {
        val currentTime = LocalDateTime.now()
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return null
        val endTime = currentEvents.first().endTime ?: return null
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

    /**
     * Private helper function that returns a map of the day of week that a eatery is open
     * to the opening time(s) or closed status (these are strings)
     *
     * e.g. For Oken, {Monday -> ["11:00 AM - 2:30 PM", "4:30 PM - 9:00 PM"], Sunday -> "Closed"}
     */
    private fun operatingHours() : Map<DayOfWeek, MutableList<String>>{
        var dailyHours = mutableMapOf<DayOfWeek, MutableList<String>>()

        events?.forEach { event ->
            val dayOfWeek = event.startTime?.dayOfWeek
            val openTime = event.startTime?.format(DateTimeFormatter.ofPattern("h:mm a"))
            val closeTime = event.endTime?.format(DateTimeFormatter.ofPattern("h:mm a"))
//            Log.d("event", event.toString())

            val timeString = "$openTime - $closeTime"

            if (dayOfWeek != null && dailyHours[dayOfWeek]?.none { it.contains(timeString) } != false) {
                dailyHours.computeIfAbsent(dayOfWeek) { mutableListOf() }.add(timeString)
            }
        }

        DayOfWeek.values().forEach { dayOfWeek ->
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
        var dailyHours = operatingHours()

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

        var formattedHoursList = formattedHours.toList().sortedBy { entry ->
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
    @Json(name = "id") val id: Int? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "start_timestamp") val startTime: LocalDateTime? = null,
    @Json(name = "end_timestamp") val endTime: LocalDateTime? = null
)

@JsonClass(generateAdapter = true)
data class WaitTimeDay(
    @Json(name = "canonical_date") val canonicalDate: Date? = null,
    @Json(name = "data") val data: List<WaitTimeData>? = null
)


@JsonClass(generateAdapter = true)
data class WaitTimeData(
    @Json(name = "timestamp") val timestamp: LocalDateTime? = null,
    @Json(name = "wait_time_low") val waitTimeLow: Int? = null,
    @Json(name = "wait_time_expected") val waitTimeExpected: Int? = null,
    @Json(name = "wait_time_high") val waitTimeHigh: Int? = null
)


@JsonClass(generateAdapter = true)
data class Event(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "event_description") val description: String? = null,
    @Json(name = "start") val startTime: LocalDateTime? = null,
    @Json(name = "end") val endTime: LocalDateTime? = null,
    @Json(name = "menu") val menu: MutableList<MenuCategory>? = null
)

@JsonClass(generateAdapter = true)
data class MenuCategory(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "event") val event: Int? = null,
    @Json(name = "items") val items: List<MenuItem>? = null
)

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "category") val category: Int? = null,
    @Json(name = "name") val name: String? = null,
)
