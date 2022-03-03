package com.appdev.eateryblueandroid.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@JsonClass(generateAdapter = true)
data class Eatery(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "menu_summary") val menuSummary: String? = null,
    @Json(name = "campus_area") val campusArea: String? = null,
    @Json(name = "events") val events: List<Event>? = null,
    @Json(name = "latitude") val latitude: Float? = null,
    @Json(name = "longitude") val longitude: Float? = null,
    @Json(name = "payment_accepts_cash") val paymentAcceptsCash: Boolean? = null,
    @Json(name = "payment_accepts_brbs") val paymentAcceptsBrbs: Boolean? = null,
    @Json(name = "payment_accepts_meal_swipes") val paymentAcceptsMealSwipes: Boolean? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "online_order_url") val onlineOrderUrl: String? = null,
    @Json(name = "wait_times") val waitTimes: List<WaitTimeDay>? = null,
    @Json(name = "alerts") val alerts: List<Alert>? = null,
    var isFavorite: Boolean = false
)

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
    @Json(name = "description") val description: String? = null,
    @Json(name = "canonical_date") val canonicalDate: Date? = null,
    @Json(name = "start_timestamp") val startTime: LocalDateTime? = null,
    @Json(name = "end_timestamp") val endTime: LocalDateTime? = null,
    @Json(name = "menu") val menu: List<MenuCategory>? = null
)

@JsonClass(generateAdapter = true)
data class MenuCategory(
    @Json(name = "category") val category: String? = null,
    @Json(name = "items") val items: List<MenuItem>? = null
)

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "healthy") val healthy: Boolean? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "base_price") val basePrice: Float? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "sections") val sections: List<MenuItemSection>? = null
)

@JsonClass(generateAdapter = true)
data class MenuItemSection(
    @Json(name = "name") val name: String? = null,
    @Json(name = "subitems") val subitems: List<MenuSubItem>? = null
)

@JsonClass(generateAdapter = true)
data class MenuSubItem(
    @Json(name = "name") val name: String? = null,
    @Json(name = "total_price") val totalPrice: Float? = null,
    @Json(name = "additional_price") val additionalPrice: Float? = null
)

fun getWalkTimes(eatery: Eatery): String {
    return "3 min walk"
}

fun getWaitTimes(eatery: Eatery): String? {
    val waitTimeData = eatery.waitTimes
    if (waitTimeData.isNullOrEmpty())
        return null
    val waitTimeDay = waitTimeData.find { waitTimeDay ->
        waitTimeDay.canonicalDate
            ?.toInstant()
            ?.truncatedTo(ChronoUnit.DAYS)
            ?.equals(Date().toInstant().truncatedTo(ChronoUnit.DAYS)) ?: true
    }?.data ?: return null
    val waitTimes = waitTimeDay.find { waitTimeData ->
        waitTimeData.timestamp?.isBefore(LocalDateTime.now()) ?: true
    } ?: return null
    return "${waitTimes.waitTimeLow?.div(60)}-${waitTimes.waitTimeHigh?.div(60)} minutes"
}

fun getCurrentEvents(eatery: Eatery): List<Event>? {
    val eventData = eatery.events
    val currentTime = LocalDateTime.now()
    if (eventData.isNullOrEmpty())
        return null
    return eventData.filter{ event ->
        currentTime.isAfter(event.startTime) && currentTime.isBefore(event.endTime)
    }
}

fun getOpenUntil(eatery: Eatery): String? {
    val currentEvents = getCurrentEvents(eatery)
    if (currentEvents.isNullOrEmpty())
        return null
    val endTime = currentEvents.first().endTime ?: return null
    return "Open until ${endTime.format(DateTimeFormatter.ofPattern("K:mm a"))}"
}

fun isClosed(eatery: Eatery): Boolean {
    return getOpenUntil(eatery) == null
}
