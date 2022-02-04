package com.appdev.eateryblueandroid.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class User(
    @Json(name="id") val id: String? = null,
    @Json(name="userName") val userName: String? = null,
    @Json(name="firstName") val firstName: String? = null,
    @Json(name="middleName") val middleName: String? = null,
    @Json(name="lastName") val lastName: String? = null,
    @Json(name="email") val email: String? = null,
    @Json(name="phone") val phone: String? = null,
    var paymentMethods: List<PaymentMethod>? = null,
    var transactions: List<Transaction>? = listOf()
)

data class PaymentMethodsResponse(
    @Json(name="accounts") val paymentMethods: List<PaymentMethod>? = null
)

data class PaymentMethod(
    @Json(name="accountDisplayName") val name: String? = null,
    @Json(name="balance") val balance: Double? = null
)

data class TransactionsResponse(
    @Json(name="totalCount") val totalCount: Int? = null,
    @Json(name="returnCapped") val returnCapped: Boolean? = null,
    @Json(name="transactions") val transactions: List<Transaction>? = null
)

data class Transaction(
    @Json(name="transactionId") val id: String? = null,
    @Json(name="amount") val amount: Int? = null,
    @Json(name="resultingBalance") val resultingBalance: Int? = null,
    @Json(name="postedDate") val date: LocalDateTime? = null,
    @Json(name="accountName") val accountName: String? = null,
    @Json(name="locationName") val type: TransactionType? = null
)

enum class TransactionType {
    DEPOSIT, SPEND, NOOP
}