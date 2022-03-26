package com.appdev.eateryblueandroid.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: String? = null,
    @Json(name = "userName") val userName: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "middleName") val middleName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    var accounts: List<Account>? = null,
    var transactions: List<Transaction>? = listOf()
)



data class AccountsResponse(
    @Json(name = "accounts") val accounts: List<Account>? = null
)

data class Account(
    @Json(name = "accountDisplayName") val type: AccountType? = null,
    @Json(name = "balance") val balance: Double? = null
)

data class TransactionsResponse(
    @Json(name = "totalCount") val totalCount: Int? = null,
    @Json(name = "returnCapped") val returnCapped: Boolean? = null,
    @Json(name = "transactions") val transactions: List<Transaction>? = null
)

data class Transaction(
    @Json(name = "transactionId") val id: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "resultingBalance") val resultingBalance: Double? = null,
    @Json(name = "postedDate") val date: LocalDateTime? = null,
    @Json(name = "transactionType") val transactionType: TransactionType? = null,
    @Json(name = "accountName") val accountType: AccountType? = null,
    @Json(name = "locationName") val location: String? = null,
)

enum class AccountType {
    // MEALSWIPES is used for transaction history filtering, only. For anything else, use the actual
    // meal plan types in the block below (OFF_CAMPUS, BEAR_TRADITIONAL, etc.).
    LAUNDRY, MEALSWIPES, BRBS, CITYBUCKS,

    OFF_CAMPUS, BEAR_TRADITIONAL, UNLIMITED, BEAR_BASIC,
    BEAR_CHOICE, HOUSE_MEALPLAN, HOUSE_AFFILIATE, FLEX, JUST_BUCKS,

    OTHER
}

enum class TransactionType {
    DEPOSIT, SPEND, NOOP
}