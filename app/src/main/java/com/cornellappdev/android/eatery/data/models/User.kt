package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "device_id") val deviceId: String = "",
    @Json(name = "fcm_token") val fcmToken: String = "",
    @Json(name = "favorite_eateries") val favoriteEateries: List<Int> = emptyList(),
    @Json(name = "favorite_items") val favoriteItems: List<String> = emptyList(),
    @Json(name = "brb_balance") val brbBalance: Double = 0.0,
    @Json(name = "city_bucks_balance") val cityBucksBalance: Double = 0.0,
    @Json(name = "laundry_balance") val laundryBalance: Double = 0.0,
    @Json(name = "brb_account_name") val brbAccountName: String = "",
    @Json(name = "city_bucks_account_name") val cityBucksAccountName: String = "",
    @Json(name = "laundry_account_name") val laundryAccountName: String = "",
    @Json(name = "userName") val userName: String = "",
    var accounts: List<Account>? = null,
    var transactions: List<Transaction>? = listOf()
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "device_id") val deviceId: String = "",
    @Json(name = "fcm_token") val fcmToken: String = "",
    @Json(name = "pin") val pin: Int = 0
)

@JsonClass(generateAdapter = true)
data class AuthorizedUser(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "device_id") val deviceId: String = "",
    @Json(name = "fcm_token") val fcmToken: String = "",
    @Json(name = "pin") val pin: Int = 0
)

@JsonClass(generateAdapter = true)
data class Account(
    @Json(name = "accountDisplayName") val type: AccountType? = null,
    @Json(name = "balance") val balance: Double? = null
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
        fun fromInt(value: Int) = TransactionType.entries.first { it.value == value }
    }
}
