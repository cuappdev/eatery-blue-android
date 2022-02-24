package com.appdev.eateryblueandroid.networking.get

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
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd’T’hh:mm:ss.SSSZ"))
    }

    @FromJson
    fun fromJson(dateTime: String): LocalDateTime {
        try {
            val simpleDateTime: LocalDateTime = LocalDateTime.parse(
                dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd’T’hh:mm:ss.SSSZ")
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
