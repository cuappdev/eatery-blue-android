package com.cornellappdev.android.eateryblue.data.models

import android.location.Location
import com.cornellappdev.android.eateryblue.util.Constants.AVERAGE_WALK_SPEED
import com.cornellappdev.android.eateryblue.util.LocationHandler
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

        var todayEvents = events.filter { event ->
            currentTime.dayOfYear == event.startTime?.dayOfYear
        }.sortedBy { it.startTime }
        // is sorting them here too slow?
        todayEvents.forEach { event ->
            var i = 0
            var chefs: MutableList<MenuCategory> = mutableListOf()
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

    fun getCurrentEvent(): Event? {
        return getTodaysEvents().find {
            it.startTime?.isBefore(LocalDateTime.now()) ?: true
                    && it.endTime?.isAfter(LocalDateTime.now()) ?: true
        }
    }

    fun getSelectedDayMeal(meal: Int, day: Int): List<Event>? {
        var currentDay = LocalDate.now()
        currentDay = currentDay.plusDays(day.toLong())
        var mealName = ""
        mealName = when (meal) {
            1 -> {
                "Breakfast"
            }

            2 -> {
                "Lunch"
            }

            else -> {
                "Dinner"
            }
        }
        return events?.filter { event ->
            currentDay.dayOfYear == event.startTime?.dayOfYear && event.description == mealName
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
        return "Open until ${endTime.format(DateTimeFormatter.ofPattern("K:mm a"))}"
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

    fun calculateTimeUntilClosing(): Flow<String>? {
        val currentTime = LocalDateTime.now()
        val currentEvents = getCurrentEvents()
        if (currentEvents.isEmpty())
            return null
        val endTime = currentEvents.first().endTime ?: return null
        var timeBuffer: Long = Duration.between(currentTime, endTime).toMinutes()


        return flow {
            while (timeBuffer in 1..30) {
                timeBuffer = Duration.between(currentTime, endTime).toMinutes()
                emit(timeBuffer.toString())
                delay(
                    /**45 seconds=*/
                    45000
                )
            }
        }
    }

    fun formatOperatingHours(): List<Pair<String, List<String>>> {
        var dailyHours = mutableMapOf<DayOfWeek, MutableList<String>>()

        events?.forEach { event ->

            val dayOfWeek = event.startTime?.dayOfWeek
            val openTime = event.startTime?.format(DateTimeFormatter.ofPattern("h:mm a"))
            val closeTime = event.endTime?.format(DateTimeFormatter.ofPattern("h:mm a"))

            val timeString = "$openTime - $closeTime"

            if (dayOfWeek != null && dailyHours[dayOfWeek]?.none { it.contains(timeString) } != false) {
                dailyHours.computeIfAbsent(dayOfWeek) { mutableListOf() }.add(timeString)
            }
        }

        DayOfWeek.values().forEach { dayOfWeek ->
            dailyHours.computeIfAbsent(dayOfWeek) { mutableListOf("Closed") }
        }

        val groupedHours = dailyHours.entries.groupBy({ it.value }, { it.key })

        return groupedHoursFormatHelper(groupedHours)
    }

    private val dayOrder = mapOf(
        "Sunday" to 1,
        "Monday" to 2,
        "Tuesday" to 3,
        "Wednesday" to 4,
        "Thursday" to 5,
        "Friday" to 6,
        "Saturday" to 7

    )

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

    private fun isNext(day1: String, day2: String): Boolean {
        if (day1 == "MONDAY") {
            return day2 == "TUESDAY"
        } else if (day1 == "TUESDAY") {
            return day2 == "WEDNESDAY"
        } else if (day1 == "WEDNESDAY") {
            return day2 == "THURSDAY"
        } else if (day1 == "THURSDAY") {
            return day2 == "FRIDAY"
        } else if (day1 == "FRIDAY") {
            return day2 == "SATURDAY"
        } else if (day1 == "SATURDAY") {
            return false
        } else if (day1 == "SUNDAY") {
            return day2 == "MONDAY"
        } else {
            return false
        }
    }

    private fun formatString(strings: List<String>): String {
        if (strings.size > 1) {
            return "${strings.first()} to ${strings.last()}"
        } else {
            return strings.first()
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
