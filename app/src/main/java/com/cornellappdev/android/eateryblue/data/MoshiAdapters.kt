package com.cornellappdev.android.eateryblue.data

import android.util.Log
import com.cornellappdev.android.eateryblue.data.models.AccountType
import com.cornellappdev.android.eateryblue.data.models.ReportSendBody
import com.cornellappdev.android.eateryblue.data.models.TransactionType
import com.cornellappdev.android.eateryblue.util.Constants.mealPlanAccountMap
import com.google.gson.Gson
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

class DateTimeAdapter {
    @ToJson
    fun toJson(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
    }

    @FromJson
    fun fromJson(dateTime: Long): LocalDateTime {
        try {
            val instant = Instant.ofEpochSecond(dateTime)
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return LocalDateTime.MIN
    }
}

class TransactionTypeAdapter {
    @ToJson
    fun toJson(transactionType: TransactionType): Int {
        return transactionType.value
    }

    @FromJson
    fun fromJson(transactionType: Int): TransactionType {
        return TransactionType.fromInt(transactionType)
    }
}

class AccountTypeAdapter {
    @ToJson
    fun toJson(accountType: AccountType): String {
        return when (accountType) {
            AccountType.BRBS -> {
                "brb"
            }
            AccountType.CITYBUCKS -> {
                "city bucks"
            }
            AccountType.LAUNDRY -> {
                "laundry"
            }
            AccountType.MEALSWIPES -> {
                "meal plan"
            }
            else -> {
                "other"
            }
        }
    }

    private fun accountNameHasMealPlan(accountName: String): Boolean {
        mealPlanAccountMap.keys.forEach {
            if (accountName.contains(it, ignoreCase = true))
                return true
        }
        return false
    }

    @FromJson
    fun fromJson(accountName: String): AccountType {
        if (accountNameHasMealPlan(accountName)) {
            mealPlanAccountMap.keys.forEach {
                if (accountName.contains(it, ignoreCase = true)) {
                    return mealPlanAccountMap[it] ?: AccountType.OTHER
                }
            }
        }

        return if (accountName.contains("brb", ignoreCase = true)) {
            AccountType.BRBS
        } else if (accountName.contains("city bucks", ignoreCase = true)) {
            AccountType.CITYBUCKS
        } else if (accountName.contains("laundry", ignoreCase = true)) {
            AccountType.LAUNDRY
        } else {
            AccountType.OTHER
        }
    }
}
