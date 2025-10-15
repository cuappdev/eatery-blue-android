package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: String? = null,
    @Json(name = "fcmToken") val fcmToken: String? = null,
    @Json(name = "deviceId") val deviceId: String? = null,
    @Json(name = "pin") val pin: Int? = null,
    @Json(name = "favorite_eateries") val favoriteEateries: List<Int>? = null,
    @Json(name = "brb_balance") val brbBalance: Double? = null,
    @Json(name = "city_bucks_balance") val cityBucksBalance: Double? = null,
    @Json(name = "laundry_balance") val laundryBalance: Double? = null,
    @Json(name = "brb_account_name") val brbAccountName: String? = null,
    @Json(name = "city_bucks_account_name") val cityBucksAccountName: String? = null,
    @Json(name = "laundry_account_name") val laundryAccountName: String? = null,
    @Json(name = "userName") val userName: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "middleName") val middleName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    var accounts: List<Account>? = null,
    var transactions: List<Transaction>? = listOf()
)

@JsonClass(generateAdapter = true)
data class AccountsResponse(
    @Json(name = "accounts") val accounts: List<Account>? = null
)

@JsonClass(generateAdapter = true)
data class Account(
    @Json(name = "accountDisplayName") val type: AccountType? = null,
    @Json(name = "balance") val balance: Double? = null
)

@JsonClass(generateAdapter = true)
data class TransactionsResponse(
    @Json(name = "totalCount") val totalCount: Int? = null,
    @Json(name = "returnCapped") val returnCapped: Boolean? = null,
    @Json(name = "transactions") val transactions: List<Transaction>? = null
)

@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "transactionId") val id: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "resultingBalance") val resultingBalance: Double? = null,
    @Json(name = "postedDate") val date: String? = null,
    // make this TransactionType later
    @Json(name = "transactionType") val transactionType: Int? = null,
    @Json(name = "accountName") val accountType: AccountType? = null,
    @Json(name = "locationName") val location: String? = null,
)

enum class AccountType {
    // MEALSWIPES is used for transaction history filtering, only. For anything else, use the actual
    // meal plan types in the block below (OFF_CAMPUS, BEAR_TRADITIONAL, etc.).
    LAUNDRY,
    MEALSWIPES,
    BRBS,
    CITYBUCKS,
    OFF_CAMPUS,
    BEAR_TRADITIONAL,
    UNLIMITED,
    BEAR_BASIC,
    BEAR_CHOICE,
    HOUSE_MEALPLAN,
    HOUSE_AFFILIATE,
    FLEX,
    JUST_BUCKS,
    OTHER
}

enum class TransactionType(val value: Int) {
    DEPOSIT(3), SPEND(1), NOOP(0), MISC(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}
