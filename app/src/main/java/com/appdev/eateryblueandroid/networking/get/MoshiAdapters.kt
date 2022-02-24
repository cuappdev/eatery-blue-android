package com.appdev.eateryblueandroid.networking.get

import android.util.Log
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.models.TransactionType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeAdapter {
    @ToJson
    fun toJson(dateTime: LocalDateTime) : String {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss.SSSZ"))
    }

    @FromJson
    fun fromJson(dateTime: String): LocalDateTime {
        try {
            val simpleDateTime: LocalDateTime = LocalDateTime.parse(
                dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            )
            return simpleDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return LocalDateTime.MIN
    }
}

class TransactionTypeAdapter {
    @ToJson
    fun toJson(transactionType: TransactionType) : Int {
        return if (transactionType == TransactionType.DEPOSIT) {
            3
        } else if (transactionType == TransactionType.SPEND) {
            1
        } else {
            0
        }
    }

    @FromJson
    fun fromJson(transactionType: Int): TransactionType {
       return if (transactionType == 1) {
           TransactionType.SPEND
       } else if (transactionType == 3) {
           TransactionType.DEPOSIT
       } else {
           TransactionType.NOOP
       }
    }
}

class AccountTypeAdapter {
    @ToJson
    fun toJson(accountType: AccountType) : String {
        return if (accountType == AccountType.BRBS) {
            "brb"
        } else if (accountType == AccountType.CITYBUCKS) {
            "city bucks"
        } else if (accountType == AccountType.LAUNDRY) {
            "laundry"
        } else if (accountType == AccountType.MEALPLAN) {
            "meal plan"
        } else {
            "other"
        }
    }

    @FromJson
    fun fromJson(accountName: String): AccountType {
        return if (accountName.contains("brb", ignoreCase = true)) {
            AccountType.BRBS
        } else if (accountName.contains("city bucks", ignoreCase = true)) {
            AccountType.CITYBUCKS
        } else if (accountName.contains("laundry", ignoreCase = true)) {
            AccountType.LAUNDRY
        } else if (accountName.contains("meal plan", ignoreCase = true) ||
                accountName.contains("off-campus value", ignoreCase = true) ||
                accountName.contains("bear traditional", ignoreCase = true)) {
            AccountType.MEALPLAN
        } else {
            AccountType.OTHER
        }
    }
}