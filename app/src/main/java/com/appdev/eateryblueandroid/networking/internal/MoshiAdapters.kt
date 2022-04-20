package com.appdev.eateryblueandroid.networking.internal

import android.util.Log
import com.appdev.eateryblueandroid.models.ReportSendBody
import com.google.gson.Gson
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TimestampAdapter {
    @ToJson
    fun toJson(time: LocalDateTime): Long {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    @FromJson
    fun fromJson(time: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())
    }
}

class DateAdapter {
    @ToJson
    fun toJson(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    @FromJson
    fun fromJson(date: String): Date {
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

class ReportAdapter {
    @ToJson
    fun toJson(report: ReportSendBody): String {
        val gson = Gson()
        val json = gson.toJson(report)
        Log.i("JsonTest", json)
        return json
    }

    @FromJson
    fun fromJson(report: String): Boolean {
        return report.contains("true", ignoreCase = true)
    }
}