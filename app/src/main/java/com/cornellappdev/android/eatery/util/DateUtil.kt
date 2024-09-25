package com.cornellappdev.android.eatery.util

import java.time.DayOfWeek
import java.time.LocalDate


fun DayOfWeek.toReadableFullName(): String =
    this.name.lowercase().replaceFirstChar { c -> c.uppercase() }

fun DayOfWeek.toReadableShortName(): String = when (this) {
    DayOfWeek.MONDAY -> "Mon"
    DayOfWeek.TUESDAY -> "Tue"
    DayOfWeek.WEDNESDAY -> "Wed"
    DayOfWeek.THURSDAY -> "Thu"
    DayOfWeek.FRIDAY -> "Fri"
    DayOfWeek.SATURDAY -> "Sat"
    DayOfWeek.SUNDAY -> "Sun"
}

/**
 * Given an int offset from the current day of the week, return the day of the week in offset days
 * Example: If day of the week is Monday, and the offset is 1, it would return Tuesday
 */
fun Int.fromOffsetToDayOfWeek(): DayOfWeek = LocalDate.now().plusDays(this.toLong()).dayOfWeek
