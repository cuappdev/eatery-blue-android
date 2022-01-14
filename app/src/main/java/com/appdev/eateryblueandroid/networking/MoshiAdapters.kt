package com.appdev.eateryblueandroid.networking

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TimestampAdapter {
    @ToJson fun toJson(time: LocalDateTime): Long {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    @FromJson
    fun fromJson(time: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault());
    }
}

class DateAdapter {
    @ToJson fun toJson(date: Date) : String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    @FromJson fun FromJson(date: String): Date {
        try {
            val simpleDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
            if (simpleDate != null) {
                return simpleDate
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date(0)
    }
}